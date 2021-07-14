package com.dxd.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dxd
 * @create 2021-06-22 9:36
 */
@Component
@Slf4j
public class SensitiveFilter {

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    /**
     *
     * @param text 输入带过滤文本
     * @return 输出过滤后的文本
     */

    //第三步、定义过滤器
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //三个指针
        TrieNode tempNode = rootNode;
        int begin = 0;
        int end = 0;
        StringBuilder res = new StringBuilder();

        while (end < text.length()){
            char c = text.charAt(end);
            if (isSymbol(c)){
                if (tempNode == rootNode){
                    res.append(c);
                    begin++;
                }
                end++;
                continue;//一定要注意此处有continue
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                res.append(text.charAt(begin));
                end = ++ begin;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()){
                res.append(REPLACEMENT);
                begin = ++ end;
                tempNode = rootNode;
            }else {
                end++;
            }
        }

        res.append(text.substring(begin));
        return res.toString();
    }

    //判断是否是特殊符号
    private boolean isSymbol(Character c){
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //初始化前缀树
    @PostConstruct
    private void init(){
        try(InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        ){
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null){
                this.addKeyword(buffer);
            }
        } catch (Exception e) {
            log.error("文件加载错误" + e);
        }
    }

    private void addKeyword(String keyword){
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++){
            Character c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            tempNode = subNode;
        }
        //此处有点区别
        tempNode.setKeywordEnd(true);
    }


    //前缀树
    private class TrieNode{
        //关键词结束标志
        private boolean isKeywordEnd = false;
        //子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}

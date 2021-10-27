package com.dxd.community.controller;

import com.dxd.community.entity.Event;
import com.dxd.community.event.EventProducer;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author dxd
 * @create 2021-07-19 16:09
 */
@Slf4j
@Controller
public class ShareController implements CommunityConstant {
    @Value("${wk.image.command}")
    private String command;
    @Value("${wk.image.storage}")
    private String storage;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/share", method = RequestMethod.GET)
    @ResponseBody
    //什么注解都不加，并且get方式，htmlUrl用?htmlUrl=*****，服务器可以获得该值
    public String share(String htmlUrl){
        String fileName = CommunityUtil.generateUUID();
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        HashMap<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);
        return CommunityUtil.getJSONString(0, null, map);
    }
    
    @RequestMapping(path = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空");
        }
        File file = new File(storage + "/" + fileName + ".png");
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int b;
            while ((b = fis.read(bytes)) != -1){
                os.write(bytes, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
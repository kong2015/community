package com.dxd.community.service;

import com.dxd.community.dao.DiscussPostMapper;
import com.dxd.community.entity.DiscussPost;
import com.dxd.community.util.SensitiveFilter;
import com.sun.mail.imap.protocol.ID;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author dxd
 * @create 2021-06-18 16:32
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter  sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int uerId, int offset, int limit){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(uerId, offset, limit);
        return discussPosts;
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
            throw new IllegalArgumentException("帖子为空");
        }
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        //异常后续统一处理
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}

package com.dxd.community.controller;

import com.dxd.community.dao.CommentMapper;
import com.dxd.community.entity.Comment;
import com.dxd.community.entity.DiscussPost;
import com.dxd.community.entity.Event;
import com.dxd.community.event.EventProducer;
import com.dxd.community.service.CommentService;
import com.dxd.community.service.DiscussPostService;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Date;

/**
 * @author dxd
 * @create 2021-06-23 18:13
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}

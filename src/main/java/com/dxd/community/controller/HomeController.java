package com.dxd.community.controller;

import com.dxd.community.entity.DiscussPost;
import com.dxd.community.entity.Page;
import com.dxd.community.entity.User;
import com.dxd.community.service.DiscussPostService;
import com.dxd.community.service.LikeService;
import com.dxd.community.service.UserService;
import com.dxd.community.util.CommunityConstant;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dxd
 * @create 2021-06-18 16:23
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;


    @RequestMapping(path="/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> maps = new ArrayList<>();
        if (discussPosts != null) {
            for (DiscussPost discussPost : discussPosts) {
                Map<String,Object> map = new HashMap<>();
                map.put("post",discussPost);
                User userById = userService.findUserById(discussPost.getUserId());
                map.put("user",userById);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount",likeCount);

                maps.add(map);
            }
        }
        model.addAttribute("discussPosts",maps);
        return "/index";
    }

    @RequestMapping(path="/error", method = RequestMethod.GET)
    public String getIndexPage(){
        return "error/500";
    }
}

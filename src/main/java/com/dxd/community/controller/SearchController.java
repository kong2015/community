package com.dxd.community.controller;

import com.dxd.community.entity.DiscussPost;
import com.dxd.community.entity.Page;
import com.dxd.community.service.ElasticsearchService;
import com.dxd.community.service.LikeService;
import com.dxd.community.service.UserService;
import com.dxd.community.util.CommunityConstant;
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
 * @create 2021-07-18 21:16
 */
@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;


//elasticsearch中从零开始，而在page分页功能中从自然数1开始

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

//        聚合数据
        List<Map<String, Object>> list = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost discussPost : searchResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
                list.add(map);
            }
        }
        model.addAttribute("discussPosts", list);
        model.addAttribute("keyword", keyword);
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        return "/site/search";
    }
}

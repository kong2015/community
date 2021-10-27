package com.dxd.community;

import com.dxd.community.entity.DiscussPost;
import com.dxd.community.service.DiscussPostService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author dxd
 * @create 2021-07-19 22:30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTests {
    @Autowired
    private DiscussPostService postService;

//    @Test
//    public void initDataForTest() {
//        for (int i = 0; i < 100000; i++) {
//            DiscussPost post = new DiscussPost();
//            post.setUserId(111);
//            post.setTitle("互联网秋招暖春计划");
//            post.setContent("xxxxxxxxxxxxxxxxxxxxx互联网XXX");
//            post.setCreateTime(new Date());
//            post.setScore(Math.random() * 2000);
//            postService.addDiscussPost(post);
//        }
//    }

    @Test
    public void testCache() {
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 0));
    }
}
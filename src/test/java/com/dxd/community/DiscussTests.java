package com.dxd.community;

import com.dxd.community.dao.DiscussPostMapper;
import com.dxd.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author dxd
 * @create 2021-06-18 16:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectcounts(){
        int i = discussPostMapper.selectDiscussPostRows(2);
        System.out.println("i = " + i);
    }

    @Test
    public void testselectDiscussPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(2, 0, 1);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println("discussPost = " + discussPost);            
        }
    }
}

package com.dxd.community;

import com.dxd.community.dao.LoginTicketMappper;
import com.dxd.community.dao.UserMapper;
import com.dxd.community.entity.LoginTicket;
import com.dxd.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author dxd
 * @create 2021-06-17 20:56
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMappper loginTicketMappper;

    @Test
    public void testUpdate(){
        int i = userMapper.updateStatus(2, 1);
        System.out.println("i = " + i);
    }

    @Test
    public void testSelect(){
//        User user = userMapper.selectById(1);
//        System.out.println("user = " + user);

//        User user1 = userMapper.selectByEmail("test@qq.com");
//        System.out.println("user = " + user1);

        User user2 = userMapper.selectByName("test");
        System.out.println("user = " + user2);
    }
    @Test
    public void testInsert(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        System.out.println("i = " + i);
    }

    @Test
    public void testSelectAnfUpdateLogin(){
        LoginTicket abc = loginTicketMappper.selectByTicket("abc");
        System.out.println("abc = " + abc);
        int abc1 = loginTicketMappper.updateStatus("abc", 1);
        System.out.println("abc1 = " + abc1);

    }

    @Test
    public void testInsertLogin(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*10*60));
        loginTicketMappper.insertLoginTicket(loginTicket);

    }

}

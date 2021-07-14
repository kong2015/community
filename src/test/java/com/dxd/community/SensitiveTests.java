package com.dxd.community;

import com.dxd.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author dxd
 * @create 2021-06-22 12:30
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter(){
        String filter = sensitiveFilter.filter("这里可以嫖@娼呀，可以@开@票@，可以赌博，哈哈哈");
        System.out.println("filter = " + filter);

        Integer integer = Integer.valueOf(2);
        System.out.println("integer = " + integer);
        Integer integer1 = Integer.valueOf("22");
        System.out.println("integer1 = " + integer1);

        int i = Integer.parseInt("222");
        System.out.println("i = " + i);
    }
}

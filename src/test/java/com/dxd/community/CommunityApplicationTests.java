package com.dxd.community;

import com.dxd.community.dao.dxdDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext(){
        System.out.println(applicationContext);
        dxdDao bean = applicationContext.getBean(dxdDao.class);
        System.out.println(bean.select());
        dxdDao dxdHibernate = applicationContext.getBean("dxdHibernate", dxdDao.class);
        System.out.println(dxdHibernate.select());
    }

    @Autowired
    @Qualifier("dxdHibernate")
    private dxdDao dxdDao;
    @Test
    public void testDI(){
        System.out.println(dxdDao);
    }




}

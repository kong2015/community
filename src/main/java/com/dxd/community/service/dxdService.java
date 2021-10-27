package com.dxd.community.service;

import com.dxd.community.dao.dxdDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author dxd
 * @create 2021-06-17 10:38
 */
@Service
@Slf4j
public class dxdService {
    @Autowired
    @Qualifier("dxdHibernate")
    private dxdDao dxdDao;

    public String find(){
        return dxdDao.select();
    }

    //该方法在多线程的环境下，被异步的调用
    @Async
    public void execute1(){
        log.debug("execute1");
    }


//取消定时任务，不然运行自动启动
//    @Scheduled(initialDelay = 10000, fixedRate = 1000)
//    public void execute2(){
//        log.debug("execute2");
//    }

}

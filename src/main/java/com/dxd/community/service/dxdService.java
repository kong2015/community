package com.dxd.community.service;

import com.dxd.community.dao.dxdDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author dxd
 * @create 2021-06-17 10:38
 */
@Service
public class dxdService {
    @Autowired
    @Qualifier("dxdHibernate")
    private dxdDao dxdDao;

    public String find(){
        return dxdDao.select();
    }
}

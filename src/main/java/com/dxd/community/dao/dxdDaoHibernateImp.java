package com.dxd.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author dxd
 * @create 2021-06-16 22:42
 */
@Repository("dxdHibernate")
public class dxdDaoHibernateImp implements dxdDao {

    @Override
    public String select() {
        return "Hibernate";
    }
}

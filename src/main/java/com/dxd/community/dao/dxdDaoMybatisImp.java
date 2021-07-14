package com.dxd.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author dxd
 * @create 2021-06-16 22:43
 */
@Repository
@Primary
public class dxdDaoMybatisImp implements dxdDao {
    @Override
    public String select() {
        return "Mybatis";
    }
}

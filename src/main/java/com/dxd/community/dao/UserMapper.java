package com.dxd.community.dao;

import com.dxd.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dxd
 * @create 2021-06-17 17:54
 */

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String  headerUrl);
    int updatePassword(int id, String password);
}

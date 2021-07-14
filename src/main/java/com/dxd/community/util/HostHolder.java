package com.dxd.community.util;

import com.dxd.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author dxd
 * @create 2021-06-21 11:03
 */
//用于代替session

@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void SetUser(User user){
        users.set(user);
    }
    public void clear(){
        users.remove();
    }
}
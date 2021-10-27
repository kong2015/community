package com.dxd.community.controller.interceptor;

import com.dxd.community.entity.User;
import com.dxd.community.service.DataService;
import com.dxd.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dxd
 * @create 2021-07-17 10:51
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
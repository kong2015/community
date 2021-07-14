package com.dxd.community.controller.interceptor;

import com.dxd.community.entity.LoginTicket;
import com.dxd.community.entity.User;
import com.dxd.community.service.UserService;
import com.dxd.community.util.CookieUtil;
import com.dxd.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author dxd
 * @create 2021-06-21 10:47
 */
@Slf4j
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取登录凭证（只有登录才可以）
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询 用户,可以在页面中使用，也可以在web层使用
                User user = userService.findUserById(loginTicket.getUserId());
//                在本次请求中持有用户（多线程)
                hostHolder.SetUser(user);
            }
        }
        log.info("pre执行");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null &&  modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
        log.info("post执行");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        log.info("after执行");
    }
}

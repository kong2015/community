package com.dxd.community.config;

import com.dxd.community.annotation.LoginRequired;
import com.dxd.community.controller.interceptor.LoginRequiredInterceptor;
import com.dxd.community.controller.interceptor.LoginTicketInterceptor;
import com.dxd.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dxd
 * @create 2021-06-21 11:05
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).
                excludePathPatterns("/**/*.js","/**/*.css","/**/*.png","/**/*.jpeg","/**/*.jpg");

        registry.addInterceptor(loginRequiredInterceptor).
                excludePathPatterns("/**/*.js","/**/*.css","/**/*.png","/**/*.jpeg","/**/*.jpg");

        registry.addInterceptor(messageInterceptor).excludePathPatterns("/**/*.js","/**/*.css","/**/*.png","/**/*.jpeg","/**/*.jpg");
    }
}

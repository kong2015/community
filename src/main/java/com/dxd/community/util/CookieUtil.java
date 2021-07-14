package com.dxd.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author dxd
 * @create 2021-06-21 10:50
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest httpServletRequest, String name){
        if (httpServletRequest == null || name ==null){
            throw new IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

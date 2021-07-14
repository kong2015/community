package com.dxd.community.controller.advice;

import com.dxd.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author dxd
 * @create 2021-06-24 21:21
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        //打印在日志上
        log.error("服务器发生异常：" + e.getMessage());
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            log.error(stackTraceElement.toString());
        }
//        返回给浏览器
        String header = request.getHeader("x-requested-with");
        //异步请求
        if ("XMLHttpRequest".equals(header)){
            response.setContentType("application/plain; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
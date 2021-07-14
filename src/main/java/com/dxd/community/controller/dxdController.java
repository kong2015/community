package com.dxd.community.controller;

import com.dxd.community.service.dxdService;
import com.dxd.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author dxd
 * @create 2021-06-17 10:38
 */
@Controller
@RequestMapping("/dxd")
public class dxdController {
    @Autowired
    private dxdService dxdService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return dxdService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String method = httpServletRequest.getMethod();
        System.out.println("method = " + method);
        String contextPath = httpServletRequest.getContextPath();
        System.out.println("contextPath = " + contextPath);
        String servletPath = httpServletRequest.getServletPath();
        System.out.println("servletPath = " + servletPath);
        httpServletResponse.setContentType("text/html;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = httpServletResponse.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.write("我是响应数据");
    }

    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String student(String name, int age){
        System.out.println("name = [" + name + "], age = [" + age + "]");
        return "success";
    }


    //响应html数据
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    public String getStudents(Model model){
        model.addAttribute("name","丁旭东");
        model.addAttribute("age",23);
        System.out.println(111);
        return "/demo/view";
    }

    @RequestMapping(path = "/teachers", method = RequestMethod.GET)
    public ModelAndView getTeachers(){
        ModelAndView model = new ModelAndView();
        model.addObject("name","丁旭东");
        model.addObject("age",24);
        model.setViewName("/demo/view");
        System.out.println(222);
        return model;
    }
    @RequestMapping(value = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String getJson(String name, int age){
        System.out.println("name = [" + name + "], age = [" + age + "]");
        String jsonString = CommunityUtil.getJSONString(0, "操作成功");
        return jsonString;
    }
}

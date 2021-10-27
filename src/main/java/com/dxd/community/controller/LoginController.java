package com.dxd.community.controller;

import com.dxd.community.entity.User;
import com.dxd.community.service.UserService;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.CommunityUtil;
import com.dxd.community.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dxd
 * @create 2021-06-19 11:50
 */
@Controller
@Slf4j
public class LoginController implements CommunityConstant{

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @Value("server.servlet.context-path")
    private String contextPath;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister(){
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin(){
        return "/site/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> register = userService.register(user);
        if (register == null || register.isEmpty()){
            model.addAttribute("msg", "注册成功");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", register.get("usernameMsg"));
            model.addAttribute("emailMsg", register.get("emailMsg"));
            model.addAttribute("passwordMsg", register.get("passwordMsg"));
            return "/site/register";
        }
    }
    // http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
//            model.addAttribute("destination","登录页");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/login");
//            model.addAttribute("destination","登录页");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
//            model.addAttribute("destination","首页");
        }
        return "/site/operate-result";
    }

    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse httpServletResponse /* HttpSession httpSession*/){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

//        httpSession.setAttribute("kaptcha", text);
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60*10);
        cookie.setPath(contextPath);
        httpServletResponse.addCookie(cookie);

        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60 *  10, TimeUnit.SECONDS);

        httpServletResponse.setContentType("image/png");

        try {
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            ImageIO.write(image,"png", outputStream);
        } catch (IOException e) {
            log.error("验证码闲响应失败:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        /*HttpSession httpSession,*/ boolean rememberme,
                        Model model, HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner){
        //因为login和kaptcha不是同一次请求，所以需要使用session
        //验证码在web层进行判断，用户名和密码在dao层进行判断，减少服务器的压力
//        String kaptcha = (String) httpSession.getAttribute("kaptcha");

        String kaptcha = null;
        try {
            if (StringUtils.isNotBlank(kaptchaOwner)) {
                String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
                kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
            }
        }catch (Exception e) {
            model.addAttribute("codeMsg", "验证码失效!");
            return "/site/login";
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)|| !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/login";
        }
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //登录成功后将cookie发放给数据库保存
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}

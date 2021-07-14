package com.dxd.community.service;

import com.dxd.community.dao.LoginTicketMappper;
import com.dxd.community.dao.UserMapper;
import com.dxd.community.entity.LoginTicket;
import com.dxd.community.entity.User;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.CommunityUtil;
import com.dxd.community.util.MailClient;
import com.dxd.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author dxd
 * @create 2021-06-18 16:24
 */
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMappper loginTicketMappper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private RedisTemplate redisTemplate;

    //此方法 经常被调用，使用redis缓存
    public User findUserById(int id){
        User user = getCache(id);
        if (user == null) {
             user = initCache(id);
        }
        return user;
//        User user = userMapper.selectById(id);
//        return user;
    }

    public Map<String, Object> register(User user){
        HashMap<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        if (userMapper.selectByName(user.getUsername()) != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "邮箱已被注册");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setActivationCode(CommunityUtil.generateUUID());
        userMapper.insertUser(user);

//        激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //使用模板引擎，利用thymeleaf，将context放到/mail/activation.html文件中，然后再利用mail包发送给邮箱
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);

            clearCache(userId);

            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg","账号不存在");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "账号未激活");
            return map;
        }
        String salt = user.getSalt();
        if (!user.getPassword().equals(CommunityUtil.md5(password + salt))){
            map.put("passwordMsg",  "密码错误");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        //ms为单位，需要乘以1000L。需要转换为long型，否则会发生数据丢失
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));

        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
//        loginTicketMappper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
//        loginTicketMappper.updateStatus(ticket, 1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,  loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket){
//        return loginTicketMappper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
    }

    public void updateHeader(int id, String url){
        userMapper.updateHeader(id, url);
        clearCache(id);
    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存中取值
    public User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    // 2.取不到时初始化缓存数据
    public User initCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectById(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 3.数据变更时清除缓存数据
    public void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}

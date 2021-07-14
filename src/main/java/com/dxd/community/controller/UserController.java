package com.dxd.community.controller;

import com.dxd.community.annotation.LoginRequired;
import com.dxd.community.entity.User;
import com.dxd.community.service.FollowService;
import com.dxd.community.service.LikeService;
import com.dxd.community.service.UserService;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.CommunityUtil;
import com.dxd.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author dxd
 * @create 2021-06-21 17:12
 */
@Controller
@Slf4j
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "您选择的图片格式错误");
            return "site/setting";
        }

//        生成随机的文件名

        String filename = CommunityUtil.generateUUID() + suffix;
        File file = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(file);
        } catch (IOException e) {
            log.error("文件上传错误" + e.getMessage());
            throw new RuntimeException("文件上传错误", e);
        }

        //更新数据库中保存的headerurl(web访问路径）
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }


    @RequestMapping(value = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);

        try (OutputStream outputStream = response.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(fileName)
        ){
            byte[] bufferChar = new byte[1024];
            int bufferLen = 0;
            while ((bufferLen = fileInputStream.read(bufferChar)) != -1){
                outputStream.write(bufferChar, 0, bufferLen);
            }
        } catch (IOException e) {
            log.error("读取头像失败" + e.getMessage());
        }
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String getUserIndex(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        System.out.println("=======================" + likeCount + "=======================");

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "site/profile";
    }
}

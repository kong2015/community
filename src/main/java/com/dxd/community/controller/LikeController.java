package com.dxd.community.controller;

import com.dxd.community.entity.Event;
import com.dxd.community.entity.User;
import com.dxd.community.event.EventProducer;
import com.dxd.community.service.LikeService;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.CommunityUtil;
import com.dxd.community.util.HostHolder;
import com.dxd.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.security.ssl.HandshakeOutStream;

import java.util.HashMap;

/**
 * @author dxd
 * @create 2021-06-25 22:40
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        HashMap<String, Object> map = new HashMap<>();

        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", entityLikeStatus);

        //触发点赞事件
        if (entityLikeStatus == 1){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }
        //只计算给帖子点赞时候的分数变化
        if (ENTITY_TYPE_POST == entityType){
            // 计算帖子的分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }
        return CommunityUtil.getJSONString(0, null, map);
    }
}
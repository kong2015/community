package com.dxd.community.event;

import com.alibaba.fastjson.JSONObject;
import com.dxd.community.entity.DiscussPost;
import com.dxd.community.entity.Event;
import com.dxd.community.entity.Message;
import com.dxd.community.service.DiscussPostService;
import com.dxd.community.service.ElasticsearchService;
import com.dxd.community.service.MessageService;
import com.dxd.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dxd
 * @create 2021-07-12 22:11
 */
@Slf4j
@Component
public class EventConsumer implements CommunityConstant {
    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if (record == null || record.value() == null){
            log.error("消息不能为空");
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null){
            log.error("消息格式转换错误");
        }

        Message message = new Message();
        message.setCreateTime(new Date());
        message.setConversationId(event.getTopic());
        message.setToId(event.getEntityUserId());
        message.setFromId(SYSTEM_USER_ID);

        HashMap<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式转换错误");
            return;
        }
        // 搜索引擎保存帖子信息
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式转换错误");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式转换错误");
            return;
        }
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String suffix = (String) event.getData().get("suffix");
        String fileName = (String) event.getData().get("fileName");
        String command = wkImageCommand + " --quality 75 " + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(command);
            log.info("生成长图成功: " + command);
        } catch (IOException e) {
            log.error("生成长图失败: " + e.getMessage());
        }
    }
}

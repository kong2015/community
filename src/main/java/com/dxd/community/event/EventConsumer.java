package com.dxd.community.event;

import com.alibaba.fastjson.JSONObject;
import com.dxd.community.entity.Event;
import com.dxd.community.entity.Message;
import com.dxd.community.service.MessageService;
import com.dxd.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    @Autowired
    private MessageService messageService;

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
}

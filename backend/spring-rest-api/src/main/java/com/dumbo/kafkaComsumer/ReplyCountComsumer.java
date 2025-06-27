package com.dumbo.kafkaComsumer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.dumbo.repository.dao.CommentDao;
import com.fasterxml.jackson.databind.ObjectMapper;

// 로직 분리로 인해 필요없어짐. 컨슈머 코드 저장용

@Service
public class ReplyCountComsumer 
{
    @Autowired
    private CommentDao commentDao;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "reply-count",
        groupId = "${spring.kafka.consumer.reply-count-group-id}",
        containerFactory = "replyCountListenerContainerFactory")
    public void consume(String message) 
    {
        try
        {
            Map<String, String> articleMap = objectMapper.readValue(message, Map.class);
            commentDao.incReplyCount(articleMap.get("comment_id"));
        } catch (IOException | SQLException e) { e.printStackTrace(); } // 여기에 카프카 동작 실패시 로깅
    }
}

package com.dumbo.kafkaComsumer;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;

// 로직 분리로 인해 필요없어짐. 컨슈머 코드 저장용

@Service
public class PostDraftConsumer 
{
    @Autowired
    private ElasticsearchClient esClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
        topics = "post-draft",
        groupId = "${spring.kafka.consumer.post-draft-group-id}",
        containerFactory = "postDraftListenerContainerFactory")
    public void consume(String message) 
    {
        // 메시지 처리 로직 작성

        // 1. 직렬화된 JSON을 Map<String, Object>로 역직렬화
        try
        {
            Map<String, Object> articleMap = objectMapper.readValue(message, Map.class);

            IndexResponse response = esClient.index(i -> i
            .index("posts")
            .id((String) articleMap.get("post_id"))
            .document(articleMap));
            // 여기에 엘라스틱서치에 게시글 저장하는 루틴 작성해야 할 듯?
        } catch (IOException e) { e.printStackTrace(); } // 여기에 카프카 동작 실패시 로깅
    }
}
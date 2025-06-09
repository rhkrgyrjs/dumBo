package com.dumbo.repository.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.dao.PostDao;
import com.dumbo.repository.dto.PostDTO;
import com.dumbo.repository.entity.Post;
import com.dumbo.repository.entity.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

import java.time.Instant;

import java.util.UUID;

import com.dumbo.util.HtmlSanitizer;

@Repository
public class PostDaoJdbcService implements PostDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    @Autowired
    private HtmlSanitizer sanitizer;

    @Autowired
    private KafkaTemplate<String, String>  kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Post createArticle(User user, PostDTO postDto) throws SQLException, JsonProcessingException
    {
        String sql = "INSERT INTO posts (es_id) VALUES (?)";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql);)
        {
            String uuid = UUID.randomUUID().toString();
            ps.setString(1, uuid);
            ps.executeUpdate();
            Post post = new Post();
            post.setEsId(uuid);

            // 여기에 카프카 설정 -> 엘라스틱서치가 postDto에 있는 값과 유저명 저장하도록
            Map<String, Object> article = new HashMap<>();
            article.put("post_id", post.getEsId());
            article.put("author_id", user.getId());
            article.put("author_nickname", user.getNickname());
            article.put("title", postDto.getTitle());
            String sanitizedHtml = sanitizer.sanitizeHtml(postDto.getContent());
            article.put("content_html", sanitizedHtml);
            article.put("content_text", sanitizer.extractText(sanitizedHtml));
            article.put("created_at", Instant.now().getEpochSecond());//LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))); // 엘라스틱서치에서 가능하다면 '현재 시간'으로 대체하는 게 더 나을 듯?
            article.put("updated_at", null);
            article.put("views", 0);

            String jsonMessage = objectMapper.writeValueAsString(article);
            kafkaTemplate.send("post-draft", jsonMessage);

            return post;
        }
    }
    
}

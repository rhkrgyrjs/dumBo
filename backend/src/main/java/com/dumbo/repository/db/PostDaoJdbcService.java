package com.dumbo.repository.db;

import java.io.IOException;
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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

import java.time.Instant;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;

import com.dumbo.repository.dto.ArticleDTO;
import com.dumbo.repository.dto.CursorResult;
import com.dumbo.util.HtmlSanitizer;

@Repository
public class PostDaoJdbcService implements PostDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    @Autowired
    private HtmlSanitizer sanitizer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ElasticsearchClient esClient;

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
            article.put("likes", 0);

            String jsonMessage = objectMapper.writeValueAsString(article);
            kafkaTemplate.send("post-draft", jsonMessage);

            return post;
        }
    }

   public CursorResult getArticles(Long createdAtCursor, String postIdCursor, int limit, boolean reverse) throws IOException 
   {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .index("posts")
            .size(limit)
            .sort(s -> s.field(f -> f.field("created_at").order(SortOrder.Desc)))
            .sort(s -> s.field(f -> f.field("post_id").order(SortOrder.Desc)))
            .source(src -> src.filter(f -> f.excludes("content_text")));

        // search_after 적용 (커서가 있으면)
        if (createdAtCursor != null && postIdCursor != null) {
            searchBuilder.searchAfter(List.of(
                FieldValue.of(createdAtCursor),
                FieldValue.of(postIdCursor)
            ));
        }

        // 검색 실행
        SearchResponse<ArticleDTO> response = esClient.search(
            searchBuilder.build(),
            ArticleDTO.class
        );

        // 결과 리스트 추출
        List<ArticleDTO> articles = response.hits().hits().stream()
            .map(Hit::source)
            .collect(Collectors.toList());

        Long nextCreatedAt = null;
        String nextPostId = null;

        if (!response.hits().hits().isEmpty()) {
            Hit<ArticleDTO> lastHit = response.hits().hits().get(response.hits().hits().size() - 1);
            List<FieldValue> sortValues = lastHit.sort();

            nextCreatedAt = sortValues.get(0).longValue();
            nextPostId = sortValues.get(1).stringValue();
        }

        boolean hasMore = response.hits().hits().size() == limit;

        return new CursorResult(articles, nextCreatedAt, nextPostId, hasMore);
    }



    
}

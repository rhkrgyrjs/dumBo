package com.dumbo.repository.dao.elasticsearch;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.util.HtmlSanitizer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Repository
public class ArticleElasticsearchDaoImpl implements ArticleElasticsearchDao
{
    @Autowired
    private ElasticsearchClient esClient;

    @Autowired
    private HtmlSanitizer sanitizer;

    public void saveArticle(String articleId, User user, PostDTO postDto) throws IOException
    {
        Map<String, Object> article = new HashMap<>();
        article.put("post_id", articleId);
        article.put("author_id", user.getId());
        article.put("author_nickname", user.getNickname());
        article.put("title", postDto.getTitle());
        String sanitizedHtml = sanitizer.sanitizeHtml(postDto.getContent());
        article.put("thumbnail_img_url", sanitizer.extractThumbnailImageUrl(sanitizedHtml));
        article.put("imgs", sanitizer.extractImageFileNames(sanitizedHtml)); // 여기에 이름만 추출
        article.put("content_html", sanitizedHtml);
        article.put("content_text", sanitizer.extractText(sanitizedHtml));
        article.put("created_at", Instant.now().getEpochSecond());
        article.put("updated_at", null);
        article.put("likes", 0);
        article.put("dislikes", 0);
        
        esClient.index(i -> i.index("posts").id((String) article.get("post_id")).document(article));
    }


    public boolean isArticleExists(String articleId) throws IOException
    {
        SearchRequest searchRequest = new SearchRequest.Builder().index("posts").query(q -> q.term(t -> t.field("post_id").value(FieldValue.of(articleId)))).size(1).build();
        SearchResponse<ArticleDTO> response = esClient.search(searchRequest, ArticleDTO.class);
        return !response.hits().hits().isEmpty();
    }


    public ArticleDTO getArticleById(String articleId) throws IOException
    {
        SearchRequest searchRequest = new SearchRequest.Builder().index("posts").query(q -> q.term(t -> t.field("post_id").value(FieldValue.of(articleId)))).size(1).build();
        SearchResponse<ArticleDTO> response = esClient.search(searchRequest, ArticleDTO.class);
        if (response.hits().hits().isEmpty()) return null; // 해당 postId의 게시글이 존재하지 않을 경우
        return response.hits().hits().get(0).source();
    }


    public void deleteArticle(String articleId) throws IOException
    {
        SearchRequest searchRequest = new SearchRequest.Builder().index("posts").query(q -> q.term(t -> t.field("post_id").value(FieldValue.of(articleId)))).size(1).build();
        SearchResponse<ArticleDTO> searchResponse = esClient.search(searchRequest, ArticleDTO.class);
        List<Hit<ArticleDTO>> hits = searchResponse.hits().hits();
        if (hits.isEmpty()) return;
        String documentId = hits.get(0).id();
        esClient.delete(d -> d.index("posts").id(documentId));
    }


    public List<String> getImageNamesByArticleId (String articleId) throws IOException
    {
        SearchRequest searchRequest = SearchRequest.of(s -> s.index("posts").size(1).query(q -> q.term(t -> t.field("post_id").value(articleId))).source(src -> src.filter(f -> f.includes("imgs"))));
        SearchResponse<Map> searchResponse = esClient.search(searchRequest, Map.class);
        List<Hit<Map>> hits = searchResponse.hits().hits();

        if (hits.isEmpty()) return Collections.emptyList();

        Hit<Map> hit = hits.get(0);
        Object imgsObj = hit.source().get("imgs");

        if (!(imgsObj instanceof List<?>)) return Collections.emptyList();

        Set<String> uniqueImageNames = new HashSet<>();
        for (Object url : (List<?>) imgsObj) if (url != null) uniqueImageNames.add(url.toString());
        
        return new ArrayList<>(uniqueImageNames);
    }


    public List<String> getImageNamesByAuthorId(String authorId) throws IOException
    {
        int batchSize = 1000;
        Set<String> uniqueUrls = new HashSet<>();
        AtomicReference<String> scrollIdRef = new AtomicReference<>();

        Query query = Query.of(q -> q.term(t -> t.field("author_id").value(authorId)));

        // 1. 최초 검색 scroll 시작
        SearchRequest searchRequest = SearchRequest.of(s -> s.index("posts").size(batchSize).scroll(Time.of(t -> t.time("2m"))).query(query).source(src -> src.filter(f -> f.includes("imgs"))));
        SearchResponse<Map> searchResponse = esClient.search(searchRequest, Map.class);
        for (Hit<Map> hit : searchResponse.hits().hits()) 
        {
            Object imageUrlsObj = hit.source().get("imgs");
            if (imageUrlsObj instanceof List<?>) for (Object url : (List<?>) imageUrlsObj) if (url != null) uniqueUrls.add(url.toString());
        }

        scrollIdRef.set(searchResponse.scrollId());

        // 2. scroll 반복 호출로 다음 문서들 조회
        while (scrollIdRef.get() != null && !scrollIdRef.get().isEmpty()) 
        {
            ScrollRequest scrollRequest = ScrollRequest.of(r -> r.scrollId(scrollIdRef.get()).scroll(Time.of(t -> t.time("2m"))));
            ScrollResponse<Map> scrollResponse = esClient.scroll(scrollRequest, Map.class);
            List<Hit<Map>> hits = scrollResponse.hits().hits();

            if (hits.isEmpty()) break;

            for (Hit<Map> hit : hits) 
            {
                Object imageUrlsObj = hit.source().get("imgs");
                if (imageUrlsObj instanceof List<?>) for (Object url : (List<?>) imageUrlsObj) if (url != null) uniqueUrls.add(url.toString());
                
            }
            scrollIdRef.set(scrollResponse.scrollId());
        }

        // 3. scroll 컨텍스트 클리어
        if (scrollIdRef.get() != null && !scrollIdRef.get().isEmpty()) esClient.clearScroll(c -> c.scrollId(scrollIdRef.get()));

        return new ArrayList<>(uniqueUrls);
    }


    public void deleteArticlesByAuthorId(String authorId) throws IOException
    {
        Query query = Query.of(q -> q.term(t -> t.field("author_id").value(authorId)));
        DeleteByQueryRequest request = DeleteByQueryRequest.of(d -> d.index("posts").query(query));
        esClient.deleteByQuery(request);
    }


    public CursorResult<ArticleDTO> getArticles(Long createdAtCursor, String articleIdCursor, int limit, boolean reverse) throws IOException
    {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
            .index("posts")
            .size(limit)
            .sort(s -> s.field(f -> f.field("created_at").order( (reverse ? SortOrder.Asc : SortOrder.Desc) )))
            .sort(s -> s.field(f -> f.field("post_id").order( (reverse ? SortOrder.Asc : SortOrder.Desc) )))
            .source(src -> src.filter(f -> f.excludes("content_text")));

        // search_after 적용 (커서가 있으면)
        if (createdAtCursor != null && articleIdCursor != null) {
            searchBuilder.searchAfter(List.of(
                FieldValue.of(createdAtCursor),
                FieldValue.of(articleIdCursor)
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

        return new CursorResult<ArticleDTO>(articles, nextCreatedAt, nextPostId, hasMore);
    }
}

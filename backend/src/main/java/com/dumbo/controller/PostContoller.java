package com.dumbo.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.validation.Valid;

import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.repository.dao.PostDao;
import com.dumbo.repository.dto.ArticleDTO;
import com.dumbo.repository.dto.CursorResult;
import com.dumbo.repository.dto.PostDTO;
import com.dumbo.repository.entity.Post;
import com.dumbo.repository.entity.User;
import com.dumbo.util.JWT;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/post")
public class PostContoller 
{
    @Autowired
    private JWT jwt;

    @Autowired
    private PostDao postDao;

    @Autowired
    private ElasticsearchClient esClient;

    // 게시글 작성 요청 API
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> post(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody PostDTO postDto)
    {
        // 1. 보낸 요청에 Access Token이 있는지 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없음"));
        // 2. 없다면, 글 작성 실패 리턴. --> 비로그인(IP로 글 작성) 기능은 나중에 구현할 것임

        String accessToken = authorizationHeader.substring(7);
        // 3. Access Token이 있다면, 유효성 검증
        User user = jwt.validateAccessToken(accessToken);
        // 4. Access Token이 유효하다면, 액세스 토큰에 저장된 UserId 가져오기.
        // 5. 유저의 정보를 담은 엔티티 받아오기
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않음"));
        
        // 6. RDBMS에 게시글 정보 생성하기 -> 엘라스틱서치, 카프카 요청은 DAO에서 함.
        Post post = null;
        try { post = postDao.createArticle(user, postDto); }
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", "게시글 업로드에 실패함")); }
        if (post == null) return ResponseEntity.status(500).body(Map.of("message", "게시글 업로드에 실패함"));

        return ResponseEntity.ok(Map.of("message", "게시글 업로드 요청 성공."));
    }

    // 게시글 조회 요청 API : 게시글 벌크로 리턴 : 커서 기반 페이징으로 구현하기
    // 한번에 리턴할 피드 갯수는 20개로 고정
    @GetMapping("")
    public ResponseEntity<Object> getPost(@RequestParam(required = false) Long createdAtCursor, @RequestParam(required = false) String postIdCursor, @RequestParam(defaultValue = "false") boolean reverse)
    {
        // 1. 게시글이 캐싱되어 있나 조회
        // 2. 게시글이 캐싱되어 있다면 캐싱된 게시글 리턴
        // ------ 인기 게시글 캐싱은 나중에 구현 : DAO에서 할 문제임

        // 3. 게시글이 캐싱되어 있지 않다면, Elasticsearch에 게시글 ID로 조회 요청

        try
        {
            return ResponseEntity.ok(postDao.getArticles(createdAtCursor, postIdCursor, 20, reverse)); // 20개만
        }
        catch (IOException e)
        {
            return ResponseEntity.status(500).body(Map.of("message", "게시글 조회 실패"));
        }
        
        // 4. 조회된 정보 리턴, 댓글은 클라이언트가 다시 요청해야 함.

    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @PathVariable String postId)
    {
        // 1. 요청에 액세스 토큰이 있나 확인
        // 2. 요청에 엑세스 토큰이 없다면 요청 거절
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없음"));
        
        // 3. 요청에 엑세스 토큰이 있다면, 유효한지 확인
        

        String accessToken = authorizationHeader.substring(7);
        User user = jwt.validateAccessToken(accessToken); // 요청을 보낸 User의 정보
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않음"));

        // 4. 액세스 토큰이 유효하다면, 접근하려는 자원의 주인 ID 확인
        String authorId = postDao.getArticleByPostId(postId).getAuthorId();
        // 5. 액세스 토큰이 유효하고, 접근하려는 자원의 주인임이 확인되었다면, 
        if (!user.getId().equals(authorId)) return ResponseEntity.status(401).body(Map.of("message", "권한을 벗어난 요청(다른 사람의 글 삭제 시도)"));
        // 6. 해당 글이 캐싱되어 있다면 캐싱된 글을 삭제하고, es에서 글 삭제 -> 캐싱 부분은 DAO에서 처리해야 할 문제
        if (!postDao.deleteArticle(postId)) return ResponseEntity.status(500).body(Map.of("message", "글 삭제 과정 중 오류 발생"));
        // 7. es에서 글 삭제 후, RDBMS에서 지우는 부분은 Kafka로 처리(지연 방지) -> kafka 처리도 DAO에서 해야 함 : 꼭 필요한가?에 대한 고민
        // 8. 이후 응답
        return ResponseEntity.ok(Map.of("message", "글 삭제됨)"));
    }


    /*
    @PatchMapping("/{postId}")
    public String getMethodName()//@RequestParam String param) 
    {
        
    }

    */
}

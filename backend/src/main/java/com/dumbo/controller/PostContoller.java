package com.dumbo.controller;

import java.io.IOException;
import java.sql.SQLException;
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

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.dto.CursorResult;
import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.Post;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.PostDao;
import com.dumbo.util.JWT;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * PostContoller
 * 
 * 게시글 작성/수정/삭제 등 게시글에 대한 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/post")
public class PostContoller 
{
    @Autowired
    private JWT jwt;

    @Autowired
    private PostDao postDao;

    /**
     * 게시글 작성 API
     * POST /post
     * 
     * @param authorizationHeader 액세스 토큰이 담긴 HTTP 헤더
     * @param postDto 게시글 제목, HTML 포멧의 게시글 본문
     * 요청 JSON 예시 :
     * {
     *      "title": <게시글 제목>,
     *      "content": <HTML 포멧의 게시글 본문>
     * }
     * 
     * @return [성공 시] 게시글 업로드 성공 메시지 / [실패 시] 게시글 업로드 실패 사유
     * 성공 시 응답 JSON 예시 :
     * { "message": "게시글 업로드 요청에 성공했습니다." }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <게시글 업로드 실패 사유> }
     */
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> post(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody PostDTO postDto)
    {
        // 1. 보낸 요청에 Access Token이 있는지 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없습니다."));
        // 2. 없다면, 글 작성 실패 리턴. --> 비로그인(IP로 글 작성) 기능은 나중에 구현할 것임

        String accessToken = authorizationHeader.substring(7);
        // 3. Access Token이 있다면, 유효성 검증
        User user = jwt.validateAccessToken(accessToken);
        // 4. Access Token이 유효하다면, 액세스 토큰에 저장된 UserId 가져오기.
        // 5. 유저의 정보를 담은 엔티티 받아오기
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않습니다."));
        
        // 6. RDBMS에 게시글 정보 생성하기 -> 엘라스틱서치, 카프카 요청은 DAO에서 함.
        Post post = null;
        try { post = postDao.createArticle(user, postDto); }
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", "게시글 업로드 중 오류가 발생했습니다.")); }
        if (post == null) return ResponseEntity.status(500).body(Map.of("message", "게시글 업로드에 실패했습니다."));

        return ResponseEntity.ok(Map.of("message", "게시글 업로드 요청에 성공했습니다."));
    }

    /**
     * 게시글 피드 요청 API
     * GET /post
     * 
     * 커서 기반 페이징 : 생성시각을 메인 커서, 게시글ID를 서브 커서로 정렬
     * 
     * @param createdAtCursor 생성 시각 커서
     * @param postIdCursor 게시글ID 커서
     * @param reverse 다시 위로 스크롤 할 때를 대비한 게시글 쿼리 순서 반전
     * 
     * @return [성공 시] 게시글 정보(ArticleDTO)가 담긴 JSON 배열, 다음 생성 시각 커서, 게시글ID 커서, 다음 게시글이 더 있는지 여부 / [실패 시] 조회 실패 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "data": <ArticleDTO 정보가 담긴 객체들의 배열>,
     *      "nextCreatedAt": <생성 시각 커서>,
     *      "id": <게시글ID 커서>,
     *      "hasMore": <해당 커서로 더 로드할 게시글이 있는지 여부>,
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": "게시글 피드 조회에 실패했습니다." }
     */
    @GetMapping("")
    public ResponseEntity<Object> getPost(@RequestParam(required = false) Long createdAtCursor, @RequestParam(required = false) String postIdCursor, @RequestParam(defaultValue = "false") boolean reverse)
    {
        // 1. 게시글이 캐싱되어 있나 조회
        // 2. 게시글이 캐싱되어 있다면 캐싱된 게시글 리턴
        // ------ 인기 게시글 캐싱은 나중에 구현 : DAO에서 할 문제임

        // 3. 게시글이 캐싱되어 있지 않다면, Elasticsearch에 게시글 ID로 조회 요청

        try { return ResponseEntity.ok(postDao.getArticleFeed(createdAtCursor, postIdCursor, 20, reverse)); } // 일단은 20개만
        catch (IOException e) { return ResponseEntity.status(500).body(Map.of("message", "게시글 피드 조회에 실패했습니다.")); }
        // 4. 조회된 정보 리턴, 댓글은 클라이언트가 다시 요청해야 함.
    }

    /**
     * 게시글 삭제 요청 API
     * DELETE /post/{postId}
     * 
     * @param authorizationHeader 액세스 토큰이 담긴 HTTP 헤더
     * @param postId 삭제할 게시글ID
     * 
     * @return [성공 시] 게시글 삭제 요청 성공 메시지 / [실패 시] 게시글 삭제 요청 실패 사유
     * 성공 시 응답 JSON 예시 :
     * { "message": "글 삭제 요청에 성공했습니다." }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": "<게시글 삭제 요청 실패 사유>" }
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @PathVariable String postId)
    {
        // 1. 요청에 액세스 토큰이 있나 확인
        // 2. 요청에 엑세스 토큰이 없다면 요청 거절
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없습니다."));
        
        // 3. 요청에 엑세스 토큰이 있다면, 유효한지 확인
        String accessToken = authorizationHeader.substring(7);
        User user = jwt.validateAccessToken(accessToken); // 요청을 보낸 User의 정보
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않습니다."));

        // 4. 액세스 토큰이 유효하다면, 접근하려는 자원의 주인 ID 확인
        String authorId = postDao.getArticleByPostId(postId).getAuthorId();
        // 5. 액세스 토큰이 유효하고, 접근하려는 자원의 주인임이 확인되었다면, 
        if (!user.getId().equals(authorId)) return ResponseEntity.status(401).body(Map.of("message", "권한을 벗어난 요청입니다."));
        // 6. 해당 글이 캐싱되어 있다면 캐싱된 글을 삭제하고, es에서 글 삭제 -> 캐싱 부분은 DAO에서 처리해야 할 문제
        try 
        {
            postDao.deleteArticle(postId); 
        } 
        catch (SQLException | IOException e) { return ResponseEntity.status(500).body(Map.of("message", "글 삭제 과정 중 오류가 발생했습니다.")); }
        // 7. es에서 글 삭제 후, RDBMS에서 지우는 부분은 Kafka로 처리(지연 방지) -> kafka 처리도 DAO에서 해야 함 : 꼭 필요한가?에 대한 고민
        // 8. 이후 응답
        return ResponseEntity.ok(Map.of("message", "글 삭제 요청에 성공했습니다."));
    }


    /*
    @PatchMapping("/{postId}")
    public String getMethodName()//@RequestParam String param) 
    {
        
    }

    */
}

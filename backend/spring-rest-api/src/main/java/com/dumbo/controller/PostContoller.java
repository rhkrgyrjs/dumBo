package com.dumbo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.domain.dto.PostDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.service.auth.AuthService;
import com.dumbo.service.post.PostService;

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
    private AuthService authServ;

    @Autowired
    private PostService postServ;

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
    public ResponseEntity<Map<String, String>> post(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody PostDTO postDto)
    {
        // 1. Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);
        
        // 2. 게시글 업로드
        postServ.createPost(user, postDto);

        // 3. 성공 메시지 리턴
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
        // 1. 피드 조회해서 리턴
        return ResponseEntity.ok(postServ.getArticleFeed(createdAtCursor, postIdCursor, 20, reverse)); // 일단은 20개만
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
        // 1. Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);

        // 2. 글 삭제
        postServ.deletePost(user, postId);

        // 3. 응답 리턴
        return ResponseEntity.ok(Map.of("message", "글 삭제 요청에 성공했습니다."));
    }


    /*
    @PatchMapping("/{postId}")
    public String getMethodName()//@RequestParam String param) 
    {
        
    }

    */
}

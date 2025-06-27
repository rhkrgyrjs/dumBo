package com.dumbo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.domain.entity.User;
import com.dumbo.service.AuthService;
import com.dumbo.service.CommentService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


/**
 * CommentController
 * 
 * 댓글/답글 작성/수정/삭제 등 댓글/답글에 대한 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/comment")
public class CommentController 
{
    @Autowired
    private AuthService authServ;

    @Autowired
    private CommentService commentServ;

    /**
     * 댓글 작성 API
     * POST /comment/{postId}
     * 
     * @param authorizationHeader 액세스 토큰이 담긴 HTTP 헤더
     * @param postId 댓글을 작성할 게시글의 ID
     * @param body 작성할 댓글의 본문
     * 요청 JSON 예시 :
     * { "content": <작성할 댓글 본문> }
     * 
     * @return [성공 시] 댓글 작성 성공 메시지 / [실패 시] 댓글 작성 실패 사유
     * 성공 시 응답 JSON 예시 :
     * { "message": "댓글 작성에 성공했습니다." }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <댓글 작성 실패 사유> }
     */
    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, String>> writeComment(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, String> body, @PathVariable String postId)
    {
        // Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);

        // 댓글 작성
        commentServ.createComment(user, postId, body.get("content"));

        // 응답 리턴
        return ResponseEntity.ok(Map.of("message", "댓글 작성에 성공했습니다."));
    }

    /**
     * 댓글 피드 요청 API
     * GET /comment/{postId}
     * 
     * 커서 기반 페이징 : 생성시각을 메인 커서, 댓글ID를 서브 커서로 정렬
     * 
     * @param createdAtCursor 생성 시각 커서
     * @param commentIdCursor 댓글ID 커서
     * 
     * @return [성공 시] 댓글 정보(CommentDTO)가 담긴 JSON 배열, 다음 생성 시각 커서, 댓글ID 커서, 다음 댓글이 더 있는지 여부 / [실패 시] 조회 실패 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "data": <CommentDTO 정보가 담긴 객체들의 배열>,
     *      "nextCreatedAt": <생성 시각 커서>,
     *      "id": <댓글ID 커서>,
     *      "hasMore": <해당 커서로 더 로드할 댓글이 있는지 여부>,
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": "댓글 조회에 실패했습니다." }
     */
    @GetMapping("/{postId}")
    public ResponseEntity<Object> fetchComment(@RequestParam(required = false) Long createdAtCursor, @RequestParam(required = false) String commentIdCursor, @RequestParam(defaultValue = "false") boolean reverse, @PathVariable String postId)
    {
        // 1. 피드 조회해서 리턴
        return ResponseEntity.ok(commentServ.getCommentFeed(postId, createdAtCursor, commentIdCursor, 20, reverse)); // 일단은 20개만
    }

    // 답글 작성하는 API
    /**
     * 답글 작성 API
     * POST /comment/{postId}/{commentId}
     * 
     * @param authorizationHeader 액세스 토큰이 담긴 HTTP 헤더
     * @param postId 답글을 작성할 게시글의 ID
     * @param commentId 답글을 작성할 댓글의 ID
     * @param body 작성할 댓글의 본문
     * 
     * @return [성공 시] 답글 작성 성공 메시지 / [실패 시] 답글 작성 실패 사유
     * 성공 시 응답 JSON 예시 :
     * { "message": "답글 작성에 성공했습니다." }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <답글 작성 실패 사유> }
     */
    @PostMapping("/{postId}/{commentId}")
    public ResponseEntity<Map<String, Object>> writeReply(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, String> body, @PathVariable String postId, @PathVariable String commentId)
    {
        // Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);

        // 답글 작성
        commentServ.createReply(user, commentId, postId, body.get("content"));

        // 응답 리턴
        return ResponseEntity.ok(Map.of("message", "답글 작성에 성공했습니다."));
    }

    // 댓글에 작성된 답글을 조회하는 API : 답글 벌크로 리턴 : 커서 기반 페이징으로 구현하기
    // 한번에 리턴할 답글 갯수는 20개로 고정
    /**
     * 답글 피드 요청 API
     * GET /comment/{postId}/{commentId}
     * 
     * 커서 기반 페이징 : 생성시각을 메인 커서, 답글ID를 서브 커서로 정렬
     * 
     * @param createdAtCursor 생성 시각 커서
     * @param replyIdCursor 답글ID 커서
     * 
     * @return [성공 시] 답글 정보(CommentDTO)가 담긴 JSON 배열, 다음 생성 시각 커서, 답글ID 커서, 다음 답글이 더 있는지 여부 / [실패 시] 조회 실패 사유
     * 성공 시 응답 JSON 예시 :
     * {
     *      "data": <CommentDTO 정보가 담긴 객체들의 배열>,
     *      "nextCreatedAt": <생성 시각 커서>,
     *      "id": <답글ID 커서>,
     *      "hasMore": <해당 커서로 더 로드할 답글이 있는지 여부>,
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <답글 조회 실패 사유> }
     */
    @GetMapping("/{postId}/{commentId}")
    public ResponseEntity<Object> fetchReply(@RequestParam(required = false) Long createdAtCursor, @RequestParam(required = false) String replyIdCursor, @PathVariable String postId, @PathVariable String commentId)
    {
        // 1. 피드 조회해서 리턴
        return ResponseEntity.ok(commentServ.getReplyFeed(postId, commentId, createdAtCursor, replyIdCursor, 20));
    }

    

}
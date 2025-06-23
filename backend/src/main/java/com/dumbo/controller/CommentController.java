package com.dumbo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.domain.dto.ArticleDTO;
import com.dumbo.domain.entity.Comment;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.CommentDao;
import com.dumbo.repository.dao.PostDao;
import com.dumbo.util.JWT;

import java.sql.SQLException;
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
    private JWT jwt;

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    // 댓글 작성하는 API
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
    public ResponseEntity<Map<String, Object>> writeComment(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, String> body, @PathVariable String postId)
    {
        // 1. 액세스 토큰 확인하는 루틴
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없습니다."));
        String accessToken = authorizationHeader.substring(7);
        // 3. Access Token이 있다면, 유효성 검증
        User user = jwt.validateAccessToken(accessToken);
        // 4. Access Token이 유효하다면, 액세스 토큰에 저장된 UserId 가져오기.
        // 5. 유저의 정보를 담은 엔티티 받아오기
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않습니다."));


        // 2. 유효하면, 게시글이 존재하는지 확인
        ArticleDTO post = postDao.getArticleByPostId(postId);
        if (post == null) return ResponseEntity.status(401).body(Map.of("message", "해당 게시글을 찾을 수 없습니다."));

        // 3. 게시글이 존재하면, 댓글 추가하기
        if (commentDao.createComment(user, postId, body.get("content"))) return ResponseEntity.ok(Map.of("message", "댓글 작성에 성공했습니다."));
        else return ResponseEntity.status(401).body(Map.of("message", "댓글 작성 중 오류가 발생했습니다."));
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
        try { return ResponseEntity.ok(commentDao.getCommentFeed(postId, createdAtCursor, commentIdCursor, 20, reverse)); } // 20개만
        catch (SQLException e) { return ResponseEntity.status(500).body(Map.of("message", "댓글 조회에 실패했습니다.")); }
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
        // 1. 액세스 토큰 확인하는 루틴
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없음"));
        String accessToken = authorizationHeader.substring(7);
        // 2. 액세스 토큰이 있다면, 유효성 검증
        User user = jwt.validateAccessToken(accessToken);
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않습니다."));

        // 3. 액세스 토큰이 유효하면, 해당 댓글이 존재하는지 확인
        try
        {
            Comment comment = commentDao.getCommentById(commentId);
            if (comment == null) return ResponseEntity.status(401).body(Map.of("message", "해당 댓글을 찾을 수 없습니다."));
            if (comment.getReplyTo() != null) return ResponseEntity.status(401).body(Map.of("message", "답글에는 답글을 작성할 수 없습니다."));

            if (commentDao.createReply(user, comment, postId, body.get("content"))) return ResponseEntity.ok(Map.of("message", "답글 작성에 성공했습니다."));
            else return ResponseEntity.status(401).body(Map.of("message", "답글 작성에 실패했습니다."));
        } catch (SQLException e) { return ResponseEntity.status(401).body(Map.of("message", "답글 작성 중 오류가 발생했습니다.")); }
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
        try
        {
            // 해당 답글이 존재하는지 확인
            Comment comment = commentDao.getCommentById(commentId);
            if (comment == null) return ResponseEntity.status(401).body(Map.of("message", "해당 댓글을 찾을 수 없습니다."));
            return ResponseEntity.ok(commentDao.getReplysByComment(comment, createdAtCursor, replyIdCursor, 20)); // 20개만
        }
        catch (SQLException e) { return ResponseEntity.status(500).body(Map.of("message", "답글 조회에 중 오류가 발생했습니다.")); }
    }

}
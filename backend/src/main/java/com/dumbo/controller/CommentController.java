package com.dumbo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.repository.dao.CommentDao;
import com.dumbo.repository.dao.PostDao;
import com.dumbo.repository.dto.ArticleDTO;
import com.dumbo.repository.entity.User;
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
    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> writeComment(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, String> body, @PathVariable String postId)
    {
        // 1. 액세스 토큰 확인하는 루틴
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 없음"));
         String accessToken = authorizationHeader.substring(7);
        // 3. Access Token이 있다면, 유효성 검증
        User user = jwt.validateAccessToken(accessToken);
        // 4. Access Token이 유효하다면, 액세스 토큰에 저장된 UserId 가져오기.
        // 5. 유저의 정보를 담은 엔티티 받아오기
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "액세스 토큰이 유효하지 않음"));


        // 2. 유효하면, 게시글이 존재하는지 확인
        ArticleDTO post = postDao.getArticleByPostId(postId);
        if (post == null) return ResponseEntity.status(401).body(Map.of("message", "해당 게시글을 찾을 수 없음"));

        // 3. 게시글이 존재하면, 댓글 추가하기
        if (commentDao.createComment(user, postId, body.get("content"))) return ResponseEntity.ok(Map.of("message", "댓글 작성 완료"));
        else return ResponseEntity.status(401).body(Map.of("message", "댓글 작성에 실패함"));
    }

    // 게시글에 작성된 댓글을 조회하는 API : 댓글 벌크로 리턴 : 커서 기반 페이징으로 구현하기
    // 한번에 리턴할 댓글 갯수는 20개로 고정
    @GetMapping("/{postId}")
    public ResponseEntity<Object> fetchComment(@RequestParam(required = false) Long createdAtCursor, @RequestParam(required = false) String commentIdCursor, @RequestParam(defaultValue = "false") boolean reverse, @PathVariable String postId)
    {
        try { return ResponseEntity.ok(commentDao.getCommentFeed(postId, createdAtCursor, commentIdCursor, 20, reverse)); } // 20개만
        catch (SQLException e) { return ResponseEntity.status(500).body(Map.of("message", "댓글 조회 실패")); }
    }

}
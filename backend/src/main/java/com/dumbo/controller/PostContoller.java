package com.dumbo.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.repository.dao.PostDao;
import com.dumbo.repository.dto.PostDTO;
import com.dumbo.repository.entity.Post;
import com.dumbo.repository.entity.User;
import com.dumbo.util.JWT;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostContoller 
{
    @Autowired
    private JWT jwt;

    @Autowired
    private PostDao postDao;

    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> draft(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody PostDTO postDto)
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
        catch (Exception e) { return ResponseEntity.status(401).body(Map.of("message", "게시글 업로드에 실패함" + e.getMessage())); }
        if (post == null) return ResponseEntity.status(401).body(Map.of("message", "게시글 업로드에 실패함 : null"));

        return ResponseEntity.ok(Map.of("message", "게시글 업로드 요청 성공."));
    }
}

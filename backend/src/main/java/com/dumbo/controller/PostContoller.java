package com.dumbo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dumbo.repository.dao.post.PostDao;
import com.dumbo.repository.dao.user.UserDao;

@RestController
@RequestMapping("/post")
public class PostContoller 
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private PostDao postDao;

    // 게시글 작성
    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> draftArticle(@RequestBody Map<String, String> body, HttpServletResponse servResponse)
    {
        // 1. 로그인 중이면 아이디로 / 비로그인 중이면 아이피로 작성자를 표시
        // 2. RDBMS에 작성글 테이블에 항목 추가
        // 3. 성공했다면 Kafka에 엘라스틱서치로 글 올려달라고 요청..
        // 4. 이후 작업은 카프카에서 완료함
    }

}

package com.dumbo.controller;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostContoller 
{
    @PostMapping("/draft")
    public ResponseEntity<Map<String, Object>> draft()
    {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "응답하는지 테스트");
        return ResponseEntity.ok(response);
    }
}

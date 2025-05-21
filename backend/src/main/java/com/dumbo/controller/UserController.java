package com.dumbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import com.dumbo.repository.dao.UserDao;
import com.dumbo.repository.dto.UserDTO;

@RestController
@RequestMapping("/users")
public class UserController 
{
    @Autowired
    private UserDao userDao;

    @PostMapping("/signup/usernameCheck")
    public ResponseEntity<Map<String, String>> usernameCheck(@RequestBody Map<String, String> body)
    {
        String username = body.get("username");
        Map<String, String> response = new HashMap<>();
        try {
            if (userDao.findUserByUsername(username) == null) {
                response.put("message", "사용 가능한 유저명입니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "사용 불가능한 유저명입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } catch (Exception e) {
            response.put("message", "유저명 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/signup/emailCheck")
    public ResponseEntity<Map<String, String>> emailCheck(@RequestBody Map<String, String> body)
    {
        String email = body.get("email");
        Map<String, String> response = new HashMap<>();
        try {
            if (userDao.findUserByEmail(email) == null) {
                response.put("message", "사용 가능한 이메일입니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "사용 불가능한 이메일입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } catch (Exception e) {
            response.put("message", "이메일 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserDTO userDto)
    {
        Map<String, String> response = new HashMap<>();
        try {
            userDao.createUser(userDto);
            response.put("message", "회원가입 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

package com.dumbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.dumbo.repository.dao.UserDao;
import com.dumbo.repository.dto.UserDTO;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController 
{
    @Autowired
    private UserDao userDao;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDto)
    {
        try 
        {
            userDao.createUser(userDto);
            return ResponseEntity.ok("회원가입 성공");
        }
        catch (Exception e) { return ResponseEntity.status(502).body("회원가입 실패" + e.getMessage());} // 임시로 에러 메시지 보냄.. 보안상 안 좋다
    }
    
}

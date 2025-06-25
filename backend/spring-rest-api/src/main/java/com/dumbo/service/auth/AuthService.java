package com.dumbo.service.auth;

import org.springframework.stereotype.Service;

import com.dumbo.domain.entity.User;

// 컨트롤러에서 사용할 간략화된 로직들

@Service
public interface AuthService 
{
    public User accessTokenValidation(String authorizationHeader); // 헤더에 담긴 엑세스 토큰이 유효한지 -> 유효하다면 해당 유저 리턴
    public User login(String email, String password); // 로그인 검증
}

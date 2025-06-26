package com.dumbo.service.auth;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dumbo.domain.dto.UserDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.exception.AccessTokenExpiredException;
import com.dumbo.exception.CookieNotFoundException;
import com.dumbo.exception.DatabaseReadException;
import com.dumbo.exception.DatabaseWriteException;
import com.dumbo.exception.LoginFailedException;
import com.dumbo.exception.MissingAccessTokenException;
import com.dumbo.exception.RefreshTokenExpiredException;
import com.dumbo.exception.UserNotFoundException;

// 컨트롤러에서 사용할 간략화된 로직들

@Service
public interface AuthService 
{
    /**
     * HTTP Request Authorization Header에서 액세스 토큰을 추출하고, 액세스 토큰의 주인(유저)을 리턴
     * 
     * @param authorizationHeader 요청에 첨부된 Authorization Header
     * @return 유저 정보
     * 
     * @throws MissingAccessTokenException 요청에 엑세스 토큰이 없을 경우
     * @throws AccessTokenExpiredException 액세스 토큰이 만료된 경우
     * @throws UserNotFoundException 유저 정보를 찾을 수 없을 경우
     */
    public User getUserFromAccessToken(String authorizationHeader) throws MissingAccessTokenException, AccessTokenExpiredException, UserNotFoundException; // 헤더에 엑세스 토큰이 담겨 있는지 확인하는 메소드
    
    public Map<String, Object> login(Map<String, String> requestBody, HttpServletResponse servletResponse) throws DatabaseReadException, LoginFailedException;
    public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse);
    public Map<String, Object> reissue(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws DatabaseReadException, RefreshTokenExpiredException, CookieNotFoundException;
    public void signup(UserDTO userDto) throws DatabaseWriteException;
    public Map<String, Object> nicknameDupCheck(String nickname) throws DatabaseReadException;
    public Map<String, Object> emailDupCheck(String email) throws DatabaseReadException;
}

package com.dumbo.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

/**
 * 인증 관련 기능 로직
 * 컨트롤러에서 사용할 간략화된 로직들을 구현
 * 로직 중 에러 발생 시 DumboException을 상속받은 예외 throw
 * 실제 구현체가 구현해야 할 메소드 정의
 */
public interface AuthService 
{
    /**
     * HTTP Request Authorization Header에서 액세스 토큰을 추출하고, 액세스 토큰의 주인(유저)을 리턴하는 메소드
     * 
     * @param authorizationHeader 요청에 첨부된 Authorization Header
     * 
     * @return 유저 정보
     * 
     * @throws MissingAccessTokenException 요청에 엑세스 토큰이 없을 경우
     * @throws AccessTokenExpiredException 액세스 토큰이 만료된 경우
     * @throws UserNotFoundException 유저 정보를 찾을 수 없을 경우
     */
    public User getUserFromAccessToken(String authorizationHeader) throws MissingAccessTokenException, AccessTokenExpiredException, UserNotFoundException; // 헤더에 엑세스 토큰이 담겨 있는지 확인하는 메소드
    
    /**
     * 로그인 메소드
     * 토큰 발급과 쿠키 설정 등을 자동화함
     * 
     * @param requestBody HTTP POST로 넘어온 이메일/비밀번호가 담긴 JSON
     * @param servletResponse 쿠키를 설정할 javax.servlet.http.HttpServletResponse
     * 
     * @return 유저에게 리턴할 응답을 포함한 객체(JSON)
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     * @throws LoginFailedException 로그인 과정 중 오류 발생
     */
    public Map<String, Object> login(Map<String, String> requestBody, HttpServletResponse servletResponse) throws DatabaseReadException, LoginFailedException;

    /**
     * 로그아웃 메소드
     * 로그아웃 요청은 실패할 수 없음
     * 리프레시 토큰을 만료시키는 쿠키 설정을 함
     * 
     * @param servletRequest 만료시킬 리프레시 토큰이 담긴 쿠키가 담긴 요청
     * @param servletResponse 쿠키 만료를 설정할 javax.servlet.http.HttpServletResponse
     */
    public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse);

    /**
     * 리프레시 토큰을 통한 토큰 재발급 메소드
     * Refresh Token Rotation : Access Token이 재발급되면, 리프레시 토큰 역시 즉시 만료되고 재발급됨
     * 
     * @param servletRequest HTTP POST로 넘어온 리프레시 토큰 쿠키가 담긴 JSON
     * @param servletResponse 쿠키를 설정할 javax.servlet.http.HttpServletResponse
     * 
     * @return 유저에게 리턴할 응답을 포함한 객체(JSON)
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     * @throws RefreshTokenExpiredException 리프레시 토큰이 만료된 경우
     * @throws CookieNotFoundException 요청에 쿠키가 없을 경우
     */
    public Map<String, Object> reissue(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws DatabaseReadException, RefreshTokenExpiredException, CookieNotFoundException;

    /**
     * 회원가입 메소드
     * 
     * @param userDto 회원가입 시킬 유저 정보
     * 
     * @throws DatabaseWriteException RDBMS/Elasticsearch 쓰기 중 오류 발생
     */
    public void signup(UserDTO userDto) throws DatabaseWriteException;

    /**
     * 닉네임 중복체크 메소드
     * 
     * @param nickname 중복체크 할 닉네임
     * 
     * @return 유저에게 리턴할 응답을 포함한 객체(JSON)
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public Map<String, Object> nicknameDupCheck(String nickname) throws DatabaseReadException;

    /**
     * 이메일 중복체크 메소드
     * 
     * @param email 중복체크 할 이메일
     * 
     * @return 유저에게 리턴할 응답을 포함한 객체(JSON)
     * 
     * @throws DatabaseReadException RDBMS/Elasticsearch 읽기 중 오류 발생
     */
    public Map<String, Object> emailDupCheck(String email) throws DatabaseReadException;
}

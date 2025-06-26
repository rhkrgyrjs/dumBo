package com.dumbo.service.auth;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.dumbo.repository.dao.UserDao;
import com.dumbo.util.JWT;

@Service
public class AuthServiceImpl implements AuthService
{
    @Autowired
    private JWT jwt;

    @Autowired
    private UserDao userDao;

    public User getUserFromAccessToken(String authorizationHeader) throws MissingAccessTokenException, AccessTokenExpiredException, UserNotFoundException
    {
        // 토큰이 헤더에 없을 경우
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) throw new MissingAccessTokenException();
        String accessToken = authorizationHeader.substring(7);

        // 토큰이 만료된 경우
        String userId = jwt.extractSubjectIfValid(accessToken);
        if (userId == null) throw new AccessTokenExpiredException();

        // 토큰과 일치하는 유저 정보가 없는 경우
        try
        {
            User user = userDao.findUserByUserId(userId);
            if (user != null) return user;
            else throw new UserNotFoundException();
        }
        // 유저 정보를 찾아오는 데 오류가 발생한 경우
        catch (SQLException e) { throw new DatabaseReadException("유저 정보를 읽어오는 데 오류가 발생했습니다."); }
    }


    public Map<String, Object> login(Map<String, String> requestBody, HttpServletResponse servletResponse) throws DatabaseReadException, LoginFailedException
    {
        // 이메일-비밀번호 검증
        User user = null;
        try { user = userDao.loginCheck(requestBody.get("email"), requestBody.get("password")); }
        catch (SQLException e) { throw new DatabaseReadException("로그인 중 오류가 발생했습니다."); }
        if (user == null) throw new LoginFailedException();

        // 액세스 토큰 + 리프레시 토큰 발급
        Map<String, String> tokens = jwt.generateRefreshTokenAndAccessToken(user.getId());
        
        // 응답 객체 생성
        Map<String, Object> response = new HashMap<>();

        // 리프레시 토큰 쿠키 설정
        servletResponse.addHeader("Set-Cookie", tokens.get("refreshTokenCookie"));
        
        // 액세스 토큰 설정
        Map<String, String> accessToken = new HashMap<>();
        accessToken.put("token", (String) tokens.get("accessToken"));
        accessToken.put("userId", user.getId());
        response.put("accessToken", accessToken);
        
        // 유저 닉네임 설정
        response.put("nickname", user.getNickname());

        // 로그인 성공 메시지 설정
        response.put("message", "로그인에 성공했습니다.");
        
        // 응답 객체 리턴
        return response;
    }


    public void logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        // 리프레시 토큰이 담긴 쿠키를 만료시키는 쿠키 설정
        servletResponse.addHeader("Set-Cookie", jwt.getInvalidateRefreshTokenCookie());

        Cookie[] cookies = servletRequest.getCookies();
        // 요청에 쿠키가 동봉되었는지 확인
        if (cookies != null)
        {
            // 요청에 쿠키가 있다면 리프레시 토큰을 포함한 쿠키가 있는지 확인
            for (Cookie cookie : cookies)
            {
                if ("refreshToken".equals(cookie.getName()))
                {
                    // 요청에 리프레시 토큰이 포함된 쿠키가 있는지 확인
                    String userId = jwt.validateRefreshToken(cookie.getValue());
                    if (userId != null)
                    {
                        // Redis에서 리프레시 토큰 삭제
                        jwt.invalidateRefreshToken(userId);
                    }
                }
            }
        }
    }


    public Map<String, Object> reissue(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws DatabaseReadException, RefreshTokenExpiredException, CookieNotFoundException
    {
        Map<String, Object> response = new HashMap<>();
        Cookie[] cookies = servletRequest.getCookies();

        // 요청에 쿠키가 동봉되었는지 확인
        if (cookies != null)
        {
            // 요청에 쿠키가 있다면 리프레시 토큰을 포함한 쿠키가 있는지 확인
            for (Cookie cookie : cookies)
            {
                if ("refreshToken".equals(cookie.getName()))
                {
                    String oldRefreshToken = cookie.getValue();
                    
                    // 리프레시 토큰이 있을 경우, 리프레시 토큰 검증
                    String userId = jwt.validateRefreshToken(oldRefreshToken);
                    User user = null;
                    try { user = userDao.findUserByUserId(userId); }
                    catch (SQLException e) { throw new DatabaseReadException("유저 정보를 읽어오는 데 오류가 발생했습니다."); }
                    
                    if (userId != null && user != null)
                    {
                        // 리프레시 토큰이 유효하다면, 액세스 토큰 + 리프레시 토큰 재발급
                        Map<String, String> tokens = jwt.generateRefreshTokenAndAccessToken(userId);

                        // 리프레시 토큰 쿠키로 등록
                        servletResponse.addHeader("Set-Cookie", tokens.get("refreshTokenCookie"));

                        // 액세스 토큰 응답에 추가
                        Map<String, String> accessToken = new HashMap<>();
                        accessToken.put("token", (String) tokens.get("accessToken"));
                        accessToken.put("userId", userId);
                        
                        response.put("accessToken", accessToken);

                        // 닉네임 응답에 추가
                        response.put("nickname", user.getNickname());

                        // 성공 메시지 추가
                        response.put("message", "토큰이 재발급되었습니다.");

                        return response;
                    }
                }
            }
            throw new RefreshTokenExpiredException();
        }
        else throw new CookieNotFoundException();
    }


    public void signup(UserDTO userDto) throws DatabaseWriteException
    {
        try { userDao.createUser(userDto); }
        catch (SQLException e) { throw new DatabaseWriteException("회원가입 중 오류가 발생했습니다."); }
    }


    public Map<String, Object> nicknameDupCheck(String nickname) throws DatabaseReadException
    {
        Map<String, Object> response = new HashMap<>();
        try
        {
            if (userDao.findUserByNickname(nickname) == null) 
            {
                response.put("useable", true);
                response.put("message", "사용 가능한 닉네임입니다.");
                return response;
            } 
            else 
            {
                response.put("useable", false);
                response.put("message", "사용 불가능한 닉네임입니다.");
                return response;
            }

        }
        catch (SQLException e) { throw new DatabaseReadException("닉네임 중복 체크 중 오류가 발생했습니다."); }
    }


    public Map<String, Object> emailDupCheck(String email) throws DatabaseReadException
    {
        Map<String, Object> response = new HashMap<>();
        try 
        {
            if (userDao.findUserByEmail(email) == null) 
            {
                response.put("useable", true);
                response.put("message", "사용 가능한 이메일입니다.");
                return response;
            } 
            else 
            {
                response.put("useable", false);
                response.put("message", "사용 불가능한 이메일입니다.");
                return response;
            }
        } 
        catch (SQLException e) { throw new DatabaseReadException("이메일 중복 체크 중 오류가 발생했습니다."); }

    }

}

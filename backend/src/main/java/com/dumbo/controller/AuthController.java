package com.dumbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.dumbo.repository.dao.UserDao;
import com.dumbo.repository.dto.UserDTO;

import com.dumbo.util.JWT;

import com.dumbo.repository.entity.User;

// 회원가입/로그인 기능을 담당하는 컨트롤러.
@RestController
@RequestMapping("/auth")
public class AuthController 
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private JWT jwt;

    // 로그인 처리 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletResponse servResponse)
    {
        String email = body.get("email");
        String password = body.get("password");
        Map<String, Object> response = new HashMap<>();
        try 
        {
            User user = userDao.loginCheck(email, password);
            if (user != null)
            {
                // 로그인 성공 시

                // 1. 액세스 토큰 + 리프레시 토큰 발급
                Map<String, String> tokens = jwt.generateRefreshTokenAndAccessToken(user.getId());
                
                // 2. 리프레시 토큰을 저장할 쿠키 설정(httpOnly)
                servResponse.addHeader("Set-Cookie", tokens.get("refreshTokenCookie"));
                
                // 3. 액세스 토큰 설정
                Map<String, String> accessToken = new HashMap<>();
                accessToken.put("token", (String) tokens.get("accessToken"));
                accessToken.put("userId", user.getId()); // userId를 같이 리턴할 필요가 있을까? 프론트에서는 자신의 userId를 모르는 게 보안상 더 나을 수도 있다.
                response.put("accessToken", accessToken);
                
                // 4. 닉네임 알려주기
                response.put("nickname", user.getNickname());

                // 5. 로그인 성공 메시지 설정
                response.put("message", "로그인에 성공했습니다.");
                
                // 6. HTTP 200
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "로그인에 실패했습니다.");
                return ResponseEntity.status(409).body(response);
            }
        } catch (Exception e) {
            response.put("message", "로그인 시도 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 로그아웃 요청 API
    // 로그아웃 요청은 실패할 수 없음.
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest servRequest, HttpServletResponse servResponse)
    {
        Map<String, Object> response = new HashMap<>();
        Cookie[] cookies = servRequest.getCookies();

        // 1. 요청에 쿠키가 동봉되었는지 확인
        if (cookies != null)
        {
            // 2. 요청에 쿠키가 있다면 리프레시 토큰을 포함한 쿠키가 있는지 확인
            for (Cookie cookie : cookies)
            {
                if ("refreshToken".equals(cookie.getName()))
                {
                    // 3. 요청에 리프레시 토큰이 포함된 쿠키가 있는지 확인
                    String userId = jwt.validateRefreshToken(cookie.getValue());
                    if (userId != null)
                    {
                        // 4. Redis에서 리프레시 토큰 삭제
                        jwt.invalidateRefreshToken(userId);
                    }
                }
            }
        }
        // 5. 유저에게 응답 보내기
        servResponse.addHeader("Set-Cookie", jwt.getInvalidateRefreshTokenCookie());
        response.put("message", "로그아웃에 성공했습니다.");
        return ResponseEntity.ok(response);
    }

    // 액세스 토큰 + 리프레시 토큰 재발급 API
    @PostMapping("reissue")
    public ResponseEntity<Map<String, Object>> reissue(HttpServletRequest servRequest, HttpServletResponse servResponse)
    {
        Map<String, Object> response = new HashMap<>();
        Cookie[] cookies = servRequest.getCookies();

        // 1. 요청에 쿠키가 동봉되었는지 확인
        if (cookies != null)
        {
            // 2. 요청에 쿠키가 있다면 리프레시 토큰을 포함한 쿠키가 있는지 확인
            for (Cookie cookie : cookies)
            {
                if ("refreshToken".equals(cookie.getName()))
                {
                    String oldRefreshToken = cookie.getValue();
                    
                    // 3. 리프레시 토큰이 있을 경우, 리프레시 토큰 검증
                    String userId = jwt.validateRefreshToken(oldRefreshToken);
                    User user = null;
                    try { user = userDao.findUserByUserId(userId); }
                    catch (SQLException e) 
                    {
                        response.put("message", "유저 정보가 명확하지 않습니다.");
                        return ResponseEntity.status(401).body(response);
                    }
                    if (userId != null && user != null)
                    {
                        // 4. 리프레시 토큰이 유효하다면, 액세스 토큰 + 리프레시 토큰 재발급
                        Map<String, String> tokens = jwt.generateRefreshTokenAndAccessToken(userId);

                        // 4.1. 리프레시 토큰 쿠키로 등록
                        servResponse.addHeader("Set-Cookie", tokens.get("refreshTokenCookie"));

                        // 4.2. 액세스 토큰 응답에 추가
                        Map<String, String> accessToken = new HashMap<>();
                        accessToken.put("token", (String) tokens.get("accessToken"));
                        accessToken.put("userId", userId); // 응답에 userId 포함할지는 좀 더 생각해 보자.
                        
                        response.put("accessToken", accessToken);

                        // 4.3. 닉네임 응답에 추가
                        response.put("nickname", user.getNickname());

                        // 4.3. 성공 메시지 추가
                        response.put("message", "토큰이 성공적으로 재발급되었습니다.");

                        return ResponseEntity.ok(response);
                    }
                }
            }
            // 예외 : 요청에 쿠키는 있지만, 리프레시 토큰을 포함한 쿠키가 없거나 유효하지 않음
            response.put("message", "리프레시 토큰이 유효하지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }
        // 예외 : 요청에 쿠키가 없음
        response.put("message", "토큰 재발급 중 오류가 발생했습니다.");
        return ResponseEntity.status(401).body(response);
        // 예외 발생(HTTP 401)시 재로그인 유도해야함
    }

    // 회원가입 중 nickname 중복체크 API
    @PostMapping("/signup/nicknameCheck")
    public ResponseEntity<Map<String, Object>> nicknameCheck(@RequestBody Map<String, String> body)
    {
        String nickname = body.get("nickname");
        Map<String, Object> response = new HashMap<>();
        try {
            if (userDao.findUserByNickname(nickname) == null) {
                response.put("useable", true);
                response.put("message", "사용 가능한 닉네임입니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("useable", false);
                response.put("message", "사용 불가능한 닉네임입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } catch (Exception e) {
            response.put("useable", false);
            response.put("message", "닉네임 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 회원가입 중 email 중복체크 API
    @PostMapping("/signup/emailCheck")
    public ResponseEntity<Map<String, Object>> emailCheck(@RequestBody Map<String, String> body)
    {
        String email = body.get("email");
        Map<String, Object> response = new HashMap<>();
        try {
            if (userDao.findUserByEmail(email) == null) {
                response.put("useable", true);
                response.put("message", "사용 가능한 이메일입니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("useable", false);
                response.put("message", "사용 불가능한 이메일입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } catch (Exception e) {
            response.put("useable", false);
            response.put("message", "이메일 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 회원가입 API
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

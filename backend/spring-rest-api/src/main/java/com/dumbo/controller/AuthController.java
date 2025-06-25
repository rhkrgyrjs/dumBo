package com.dumbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.dumbo.domain.dto.UserDTO;
import com.dumbo.domain.entity.User;
import com.dumbo.repository.dao.UserDao;
import com.dumbo.util.JWT;

/**
 * AuthController
 * 
 * 로그인/회원가입 등 인증과 관련된 기능을 담당하는 API들이 존재하는 컨트롤러
 */
@RestController
@RequestMapping("/auth")
public class AuthController 
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private JWT jwt;

    /**
     * 로그인 API
     * POST /auth/login
     * 
     * @param body 로그인에 사용될 이메일, 비밀번호
     * 요청 JSON 예시 :
     * {
     *      "email": "user@example.com",
     *      "password": "Aa123456!"
     * }
     * 
     * @return [성공 시] 닉네임, 액세스 토큰, 로그인 성공 메시지, 리프레시 토큰 쿠키 설정 / [실패 시] 로그인 실패 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "nickname": "짱짱맨",
     *      "accessToken": {"userId": <유저 고유 UUID>, "token": <토큰 스트링>},
     *      "message": "로그인에 성공했습니다"
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <재발급 실패 사유> }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletResponse servResponse)
    {
        // 이메일, 패스워드 String 요청 body에서 추출
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
                accessToken.put("userId", user.getId());
                response.put("accessToken", accessToken);
                
                // 4. 닉네임 설정
                response.put("nickname", user.getNickname());

                // 5. 로그인 성공 메시지 설정
                response.put("message", "로그인에 성공했습니다.");
                
                // 6. 응답 리턴
                return ResponseEntity.ok(response);
            } 
            else
            {
                // 로그인 실패 시
                response.put("message", "로그인에 실패했습니다.");
                return ResponseEntity.status(409).body(response);
            }
        } 
        catch (Exception e)
        {
            // 로그인 시도 중 예외가 발생했을 경우
            response.put("message", "로그인 시도 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 로그아웃 API
     * POST /auth/logout
     * 
     * 로그아웃 요청을 실패할 수 없음. 이미 로그아웃 중이거나 로그아웃을 성공한 경우밖에 없기 때문
     * 
     * @param body 리프레시 토큰이 담긴 쿠키 동봉 POST
     * 
     * @return 리프레시 토큰이 담긴 쿠키 즉시 만료
     * 응답 JSON 예시 :
     * { "message": "로그아웃에 성공했습니다." }
     */
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
    /**
     * 액세스 토큰 + 리프레시 토큰(쿠키) 재발급 API
     * POST /auth/reissue
     * 
     * @param body 리프레시 토큰이 담긴 쿠키 동봉 POST
     * 
     * @return [성공 시] 닉네임, 액세스 토큰, 토큰 재발급 성공 메시지, 리프레시 토큰 쿠키 설정 / [실패 시] 토큰 재발급 실패 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "nickname": "짱짱맨",
     *      "accessToken": {"userId": <유저 고유 UUID>, "token": <토큰 스트링>},
     *      "message": "토큰이 성공적으로 재발급되었습니다."
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": <재발급 실패 사유> }
     */
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

                        // 5. 리프레시 토큰 쿠키로 등록
                        servResponse.addHeader("Set-Cookie", tokens.get("refreshTokenCookie"));

                        // 6. 액세스 토큰 응답에 추가
                        Map<String, String> accessToken = new HashMap<>();
                        accessToken.put("token", (String) tokens.get("accessToken"));
                        accessToken.put("userId", userId); // 응답에 userId 포함할지는 좀 더 생각해 보자.
                        
                        response.put("accessToken", accessToken);

                        // 7. 닉네임 응답에 추가
                        response.put("nickname", user.getNickname());

                        // 8. 성공 메시지 추가
                        response.put("message", "토큰이 성공적으로 재발급되었습니다.");

                        return ResponseEntity.ok(response);
                    }
                }
            }
            // 요청에 쿠키는 있지만, 리프레시 토큰을 포함한 쿠키가 없거나 유효하지 않은 경우
            response.put("message", "리프레시 토큰이 유효하지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }
        // 요청에 쿠키가 없는 경우
        response.put("message", "토큰 재발급 중 오류가 발생했습니다.");
        return ResponseEntity.status(401).body(response);
        // 예외 발생(HTTP 401)시 재로그인 유도해야함
    }

    // 회원가입 API
    /**
     * 회원가입 API
     * POST /auth/signup
     * 
     * @param userDto 사용할 닉네임, 이메일, 비밀번호
     * 요청 JSON 예시 :
     * {
     *      "email": "user@example.com",
     *      "nickname": "짱짱맨",
     *      "password": "Aa123456!"
     * }
     * 
     * @return [성공 시] 회원가입 성공 메시지 / [실패 시] 회원가입 실패 메시지
     * 성공 시 응답 JSON 예시 :
     * { "message": "회원가입에 성공했습니다." }
     * 
     * 실패 시 응답 JSON 예시 :
     * { "message": "회원가입 중 오류가 발생했습니다." }
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserDTO userDto)
    {
        Map<String, String> response = new HashMap<>();
        try 
        {
            userDao.createUser(userDto);
            response.put("message", "회원가입에 성공했습니다.");
            return ResponseEntity.ok(response);
        } 
        catch (Exception e) 
        {
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 닉네임 중복체크 API
     * GET /auth/signup/nickname-check
     * 
     * @param nickname 중복체크 할 닉네임
     * 
     * @return 사용 가능 여부, 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "useable": <boolean 값>,
     *      "message": <사용 가능/불가능 여부>
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * {
     *      "useable": false,
     *      "message": "닉네임 중복 체크 중 문제가 발생했습니다."
     * }
     */
    @GetMapping("/signup/nickname-check")
    public ResponseEntity<Map<String, Object>> nicknameCheck(@RequestParam("nickname") String nickname)
    {
        Map<String, Object> response = new HashMap<>();
        try 
        {
            if (userDao.findUserByNickname(nickname) == null) 
            {
                response.put("useable", true);
                response.put("message", "사용 가능한 닉네임입니다.");
                return ResponseEntity.ok(response);
            } 
            else 
            {
                response.put("useable", false);
                response.put("message", "사용 불가능한 닉네임입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } 
        catch (Exception e) 
        {
            response.put("useable", false);
            response.put("message", "닉네임 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 이메일 중복체크 API
     * GET /auth/signup/email-check
     * 
     * @param email 중복체크 할 이메일
     * 
     * @return 사용 가능 여부, 메시지
     * 성공 시 응답 JSON 예시 :
     * {
     *      "useable": <boolean 값>,
     *      "message": <사용 가능/불가능 여부>
     * }
     * 
     * 실패 시 응답 JSON 예시 :
     * {
     *      "useable": false,
     *      "message": "이메일 중복 체크 중 문제가 발생했습니다."
     * }
     */
    @GetMapping("/signup/email-check")
    public ResponseEntity<Map<String, Object>> emailCheck(@RequestParam("email") String email)
    {
        Map<String, Object> response = new HashMap<>();
        try 
        {
            if (userDao.findUserByEmail(email) == null) 
            {
                response.put("useable", true);
                response.put("message", "사용 가능한 이메일입니다.");
                return ResponseEntity.ok(response);
            } 
            else 
            {
                response.put("useable", false);
                response.put("message", "사용 불가능한 이메일입니다.");
                return ResponseEntity.status(409).body(response);
            }
        } 
        catch (Exception e) 
        {
            response.put("useable", false);
            response.put("message", "이메일 중복 체크 중 문제가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }

    // Patch : 개인정보 수정하기
    // DELETE : 회원탈퇴하기
    // 메소드 만들어야 함
}

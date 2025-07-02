package com.dumbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.dumbo.domain.entity.User;
import com.dumbo.domain.dto.UserModifyDTO;
import com.dumbo.domain.dto.UserRegisterDTO;
import com.dumbo.service.AuthService;



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
    private AuthService authServ;

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
        // 로그인 처리
        return ResponseEntity.ok(authServ.login(body, servResponse));
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
        // 로그아웃 처리 : 로그아웃 처리는 실패하지 않음
        authServ.logout(servRequest, servResponse);
        return ResponseEntity.ok(Map.of("message", "로그아웃에 성공했습니다."));
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
        // 토큰 재발급 처리
        return ResponseEntity.ok(authServ.reissue(servRequest, servResponse));
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
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserRegisterDTO userDto)
    {
        // 회원가입 처리
        authServ.signup(userDto);
        return ResponseEntity.ok(Map.of("message", "회원가입에 성공했습니다."));
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
        // 닉네임 중복체크 처리
        return ResponseEntity.ok(authServ.nicknameDupCheck(nickname));
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
        // 이메일 중복체크 처리
        return ResponseEntity.ok(authServ.emailDupCheck(email));
    }

    @PatchMapping("/profile")
    public ResponseEntity<Map<String, String>> updateUser(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody UserModifyDTO userDto)
    {
        // Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);

        // 유저 정보 수정
        authServ.modifyUserInfo(user, userDto);

        // 응답 리턴
        return ResponseEntity.ok(Map.of("message", "유저 정보 수정에 성공했습니다."));

    }

    @DeleteMapping("/resign")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestHeader(name = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, String> body, HttpServletResponse servResponse)
    {
        // Access Token 유효성 검증
        User user = authServ.getUserFromAccessToken(authorizationHeader);

        // 비밀번호 검증
        // 회원 탈퇴(정보 삭제 + 리프레시 토큰 바로 만료 + 썻던 글에 있는 사진들 모두 삭제)
        authServ.deleteUser(user, body.get("password"), servResponse);

        // 응답 리턴
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴에 성공했습니다."));
    }
}

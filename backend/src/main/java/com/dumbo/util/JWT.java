package com.dumbo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.dumbo.repository.db.nosql.redis.Redis;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import java.time.Duration;

/*
 * 어떤 메소드가 필요한가? -> UX를 기반으로 생각해 보자.
 * 1. 로그인 시 : 리프레시 토큰과 액세스 토큰 발급
 * 2. 로그아웃 시 : 리프레시 토큰 만료시키기
 * 3. 권한이 필요한 요청 시 : 액세스 토큰 검증
 * 4. 액세스 토큰 만료 시 : 리프레시 토큰 검증하고 액세스 토큰 발급
 * 5. 리프레시 토큰 만료 시 : 다시 로그인 유도
 * 
 * -> 기능별로 메소드를 합치지 말고, 발급/검증으로 나누자.
 * -> 작업 단위(발급/검증) 메소드는 private으로 객체 내부에서만 사용하고, 노출되는 메소드는 'batch 단위'로 설계해 토큰 관리에 일관성을 유지하자
 */

public class JWT 
{
    private final SecretKey SECRET_KEY;
    private final long ACCESS_TOKEN_EXP_TIME;
    private final long REFRESH_TOKEN_EXP_TIME;

    @Autowired
    private Redis redis;

    public JWT(SecretKey secretKey, long accessTokenExpTime, long refreshTokenExpTime)
    {
        this.SECRET_KEY = secretKey;
        this.ACCESS_TOKEN_EXP_TIME = accessTokenExpTime;
        this.REFRESH_TOKEN_EXP_TIME = refreshTokenExpTime;
    }

    // 유저의 리프레시 토큰을 담은 쿠키를 만료시키기 위한 RequestCookie
    private String invalidateRefreshTokenCookie = (ResponseCookie
    .from("refreshToken", "")
    .path("/dumbo-backend/auth/reissue")
    .maxAge(0)
    .httpOnly(true)
    .secure(false)
    .sameSite("Lax")
    .build()).toString();

    // 토큰의 만료 + 유저가 특정 자원(자원의 생성자인지)에 접근할 권한이 있는지 검증
    public boolean validateAccessToken(String accessToken, String userId)
    {
        if (accessToken == null) return false;
        try
        {
            // 1. JWT 토큰이 유효한가?
            Claims claims = Jwts.parserBuilder()
                            .setSigningKey(SECRET_KEY)
                            .build()
                            .parseClaimsJws(accessToken)
                            .getBody();
            
            // 2. 토큰이 해당 자원에 접근 가능한 권한을 가지고 있는가?
            return claims.getSubject().equals(userId);

        } catch (JwtException e) { return false; }
    }

    // 리프레시 토큰이 유효한지 검증
    // 검증 성공시 userId, 검증 실패시 null 반환
    public String validateRefreshToken(String refreshToken)
    {
        try
        {
            // 1. JWT 토큰이 유효한가?
            Claims claims = Jwts.parserBuilder()
                            .setSigningKey(SECRET_KEY)
                            .build()
                            .parseClaimsJws(refreshToken)
                            .getBody();

            // 2. 받은 토큰의 키값이 Redis에 존재하는가?
            String userId = claims.getSubject();
            // 2.1. 만약 만료시킨 토큰을 받았을 경우
            if (userId.equals("")) { return null; }
            String storedToken = redis.getRefreshToken(userId);

            // 3. 받은 토큰이 Redis에 존재하는 토큰과 일치하는가?
            if (refreshToken.equals(storedToken)) return userId;
            else return null;

        } catch (JwtException | IllegalArgumentException e) { return null; }
    }

    // 액세스 토큰 생성
    // 리프레시 토큰 만료 시 null 반환 -> 로그인(리프레시/액세스 토큰 재발급) 유도해야 함.
    public String generateAccessToken(String refreshToken)
    {
        String userId = validateRefreshToken(refreshToken);
        if (userId == null) return null;

        String accessToken = Jwts.builder()
                                .setSubject(userId)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_TIME))
                                .signWith(SECRET_KEY)
                                .compact();
        return accessToken;
    }

    // 리프레시 토큰 + 액세스 토큰 생성
    // 어차피 리프레시 토큰이 생성될 때, 엑세스 토큰도 같이 생성해야 함
    // 리프레시 토큰은 'Set-Cookie의 헤더 형태'로 리턴
    public Map<String, String> generateRefreshTokenAndAccessToken(String userId)
    {
        Date refreshTokenExpDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_TIME);
        String refreshToken = Jwts.builder()
                                .setSubject(userId)
                                .setIssuedAt(new Date())
                                .setExpiration(refreshTokenExpDate)
                                .signWith(SECRET_KEY)
                                .compact();
        redis.saveRefreshToken(userId, refreshToken, refreshTokenExpDate);

        String accessToken = Jwts.builder()
                                .setSubject(userId)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_TIME))
                                .signWith(SECRET_KEY)
                                .compact();

        Map<String, String> tokens = new HashMap<>();
        ResponseCookie refreshTokenCookie = ResponseCookie  // 토큰 정책 설정은 개발 완료 단계에서 다시 고민
        .from("refreshToken", refreshToken)
        .httpOnly(true)
        .secure(false)
        .path("/dumbo-backend/auth/reissue")
        .maxAge(Duration.ofSeconds(REFRESH_TOKEN_EXP_TIME / 1000))
        .sameSite("Lax")
        .build();
        tokens.put("refreshTokenCookie", refreshTokenCookie.toString());
        tokens.put("accessToken", accessToken);
        return tokens;
    }

    // 리프레시 토큰 Redis에서 삭제(로그아웃 요청 처리용)
    public void invalidateRefreshToken(String userId) { redis.deleteRefreshToken(userId); }

    public String getInvalidateRefreshTokenCookie() { return this.invalidateRefreshTokenCookie; }
}

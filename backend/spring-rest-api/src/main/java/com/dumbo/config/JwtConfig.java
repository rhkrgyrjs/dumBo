package com.dumbo.config;

/*
 * JwtConfig
 * 
 * Jwt 토큰의 비밀 키, 액세스/리프레시 토큰의 유효 기간 설정 파일
 * application.properties 의 설정값에 의존
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

import com.dumbo.util.JWT;

@Configuration
@PropertySource("classpath:application.properties")
public class JwtConfig 
{
    @Value("${dumbo.jwt.base64-secret-key}")
    private String base64SecretKey;

    @Value("${dumbo.jwt.access-token-exp-time}")
    private long accessTokenExpTime;

    @Value("${dumbo.jwt.refresh-token-exp-time}")
    private long refreshTokenExpTime;

    @Bean
    public JWT jwt() 
    {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return new JWT(secretKey, accessTokenExpTime, refreshTokenExpTime);
    }
}

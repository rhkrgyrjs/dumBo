package com.dumbo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import com.dumbo.util.JWT;

import io.jsonwebtoken.security.Keys;

@Configuration
@PropertySource("classpath:application.properties")
public class JwtConfig 
{
    @Value("${jwt.base64-secret-key}")
    private String base64SecretKey;

    @Value("${jwt.access-token-exp-time}")
    private long accessTokenExpTime;

    @Value("${jwt.refresh-token-exp-time}")
    private long refreshTokenExpTime;

    @Bean
    public JWT jwt() 
    {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return new JWT(secretKey, accessTokenExpTime, refreshTokenExpTime);
    }
}

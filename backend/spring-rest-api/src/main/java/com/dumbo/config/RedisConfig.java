package com.dumbo.config;

/*
 * RedisConfig
 * 
 * Redis의 서버 주소/포트/비밀번호/DB 인덱스 등 접속 정보를 받아오기 위한 설정 파일
 * application.properties 의 설정값에 의존
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.dumbo.repository.redis.Redis;

import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig 
{
    @Value("${dumbo.redis.host}")
    private String host;

    @Value("${dumbo.redis.port}")
    private int port;

    @Value("${dumbo.redis.password}")
    private String password;

    @Value("${dumbo.redis.database}")
    private int database;

    @Bean
    public Redis redis() { return new Redis(host, port, URLEncoder.encode(password, StandardCharsets.UTF_8), database); }
}

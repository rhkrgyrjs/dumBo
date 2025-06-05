package com.dumbo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.dumbo.repository.db.nosql.redis.Redis;

import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfig 
{
    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.database}")
    private int database;

    @Bean
    public Redis redis() { return new Redis(host, port, URLEncoder.encode(password, StandardCharsets.UTF_8), database); }
}

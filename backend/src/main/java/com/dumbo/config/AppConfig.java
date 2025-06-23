package com.dumbo.config;

/*
 * AppConfig
 * 
 * Spring Application 설정 파일
 */

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.dumbo")  
public class AppConfig implements WebMvcConfigurer
{
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
        .allowedOrigins("http://localhost:3000")
        .allowedMethods("POST", "GET", "PATCH", "DELETE")
        .allowedHeaders("*")
        .allowCredentials(true);
    }
}

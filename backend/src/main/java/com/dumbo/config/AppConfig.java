package com.dumbo.config;  // 이 파일이 속한 패키지 선언

import org.springframework.context.annotation.ComponentScan;  // 컴포넌트 스캔을 위한 어노테이션
import org.springframework.context.annotation.Configuration;  // 해당 클래스가 설정 클래스임을 표시
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;  // Spring MVC 사용 활성화
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // 이 클래스는 자바 기반 설정 클래스임을 의미 (XML 설정 대체)
@EnableWebMvc   // Spring MVC 기능을 사용 가능하게 함 (DispatcherServlet 내부 설정 등 포함)
@ComponentScan(basePackages = "com.dumbo")  
// com.example 패키지 및 하위 패키지에서 @Component, @Controller, @Service 등을 자동 검색
public class AppConfig implements WebMvcConfigurer
{
    // 별도 메서드 없이 설정만 담당하는 클래스

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

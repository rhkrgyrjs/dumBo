package com.dumbo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

// src/main/resources/application.properties 에서 설정값을 가져와, DriverManagerDataSource로 DB 커넥션을 가져옴
// 사용하는 DB나 접속 방식이 바뀌어도 application.properties를 수정해 바꿀 수 있다.
@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig 
{
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource()
    {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }
}

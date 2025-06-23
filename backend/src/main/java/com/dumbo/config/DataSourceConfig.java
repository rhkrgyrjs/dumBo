package com.dumbo.config;

/*
 * DataSourceConfig
 * 
 * RDBMS 연결을 javax.sql.DataSource 로 통일해서 받아오기 위한 설정 파일
 * pplication.properties 의 설정값에 의존
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig 
{
    @Value("${dumbo.datasource.url}")
    private String url;

    @Value("${dumbo.datasource.username}")
    private String username;

    @Value("${dumbo.datasource.password}")
    private String password;

    @Value("${dumbo.datasource.driver-class-name}")
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

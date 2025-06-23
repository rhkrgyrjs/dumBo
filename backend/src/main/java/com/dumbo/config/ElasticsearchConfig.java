package com.dumbo.config;

/*
 * ElasticsearchConfig
 * 
 * co.elastic.clients.elasticsearch.ElasticsearchClient 를 스프링 빈으로 등록하기 위한 설정 파일
 * application.properties 의 설정값에 의존
 */

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
@PropertySource("classpath:application.properties")
public class ElasticsearchConfig
{
    @Value("${dumbo.elasticsearch.host}")
    private String host;

    @Value("${dumbo.elasticsearch.port}")
    private int port;

    @Bean
    public ElasticsearchClient elasticsearchClient()
    {
         RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();
         RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
         return new ElasticsearchClient(transport);
    }
}

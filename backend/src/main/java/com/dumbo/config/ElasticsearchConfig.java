package com.dumbo.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig 
{
    @Bean
    public ElasticsearchClient elasticsearchClient()
    {
         RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
         RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
         return new ElasticsearchClient(transport);
    }
}

package com.lzlj.account.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Swagger 聚合配置
 * 网关统一聚合所有微服务的 API 文档
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SwaggerAggregatorConfig {

    @Value("${swagger.services:store-user,store-goods,store-member,store-promotion,store-pay,store-trade,store-flashsale,store-search,store-data,store-file,store-delivery,store-settlement}")
    private List<String> services;

    @Value("${spring.cloud.nacos.discovery.server-addr:127.0.0.1:8848}")
    private String nacosAddr;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Bean
    public WebClient webClient() {
        return webClientBuilder.build();
    }

    public List<String> getServices() {
        return services;
    }
}

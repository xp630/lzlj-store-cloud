package com.lzlj.account.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Swagger 聚合配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SwaggerAggregatorConfig {

    @Value("${swagger.services:}")
    private List<String> services;

    private final WebClient.Builder webClientBuilder;

    @Bean
    public WebClient webClient() {
        return webClientBuilder.build();
    }

    public List<String> getServices() {
        return services;
    }
}

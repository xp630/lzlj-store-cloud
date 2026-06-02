package com.lzlj.account.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * RestTemplate 配置
 */
@Configuration
@EnableConfigurationProperties(SaaSApiConfig.class)
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);   // 5秒连接超时
        factory.setReadTimeout(30000);     // 30秒读取超时

        RestTemplate restTemplate = new RestTemplate(factory);

        // 显式设置默认字符集为 UTF-8，避免中文乱码
        restTemplate.getMessageConverters().stream()
                .filter(c -> c instanceof StringHttpMessageConverter)
                .findFirst()
                .ifPresent(c -> ((StringHttpMessageConverter) c).setDefaultCharset(StandardCharsets.UTF_8));

        return restTemplate;
    }
}

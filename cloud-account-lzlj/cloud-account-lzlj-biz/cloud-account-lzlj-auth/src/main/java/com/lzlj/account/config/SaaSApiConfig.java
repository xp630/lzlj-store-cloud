package com.lzlj.account.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SaaS 服务调用配置
 */
@Data
@ConfigurationProperties(prefix = "saas.api")
public class SaaSApiConfig {

    /**
     * SaaS 服务基础地址（如：http://localhost:9092）
     */
    private String baseUrl;

    /**
     * API Key（用于调用 SaaS OpenAPI）
     */
    private String apiKey;

    /**
     * API Secret
     */
    private String apiSecret;

    /**
     * 是否启用 SaaS 调用
     */
    private boolean enabled = true;
}

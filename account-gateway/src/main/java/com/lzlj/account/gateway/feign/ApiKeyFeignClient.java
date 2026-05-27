package com.lzlj.account.gateway.feign;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * API密钥认证信息（Feign响应）
 */
@Data
class ApiKeyAuthInfo {
    private Long id;
    private String apiKey;
    private String apiSecret;
    private Long tenantId;
    private Integer status;
}

/**
 * 认证服务 Feign 客户端
 */
@FeignClient(name = "saas-auth", path = "/openapi/key")
public interface ApiKeyFeignClient {

    @GetMapping("/auth/{apiKey}")
    ApiKeyAuthInfo getAuthInfo(@PathVariable("apiKey") String apiKey);
}

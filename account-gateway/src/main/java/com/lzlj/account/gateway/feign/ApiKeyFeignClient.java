package com.lzlj.account.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 认证服务 Feign 客户端
 */
@FeignClient(name = "saas-auth", path = "/openapi/key")
public interface ApiKeyFeignClient {

    @GetMapping("/auth/{apiKey}")
    ApiKeyAuthInfo getAuthInfo(@PathVariable("apiKey") String apiKey);
}

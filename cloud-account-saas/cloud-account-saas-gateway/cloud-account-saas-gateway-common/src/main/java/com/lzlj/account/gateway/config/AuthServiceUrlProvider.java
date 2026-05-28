package com.lzlj.account.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证服务地址提供者
 * 缓存服务地址，避免重复查询
 */
@Slf4j
@Component
public class AuthServiceUrlProvider {

    private static final String AUTH_SERVICE_ID = "saas-auth";

    private final DiscoveryClient discoveryClient;

    public AuthServiceUrlProvider(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * 获取 auth 服务地址
     */
    public String getAuthServiceUrl() {
        List<ServiceInstance> instances = discoveryClient.getInstances(AUTH_SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            log.error("未找到 auth 服务实例: {}", AUTH_SERVICE_ID);
            return null;
        }
        ServiceInstance instance = instances.get(0);
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }
}

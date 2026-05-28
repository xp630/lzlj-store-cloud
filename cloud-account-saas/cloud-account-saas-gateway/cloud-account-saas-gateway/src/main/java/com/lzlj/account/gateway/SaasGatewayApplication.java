package com.lzlj.account.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 网关启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@SpringBootApplication
public class SaasGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasGatewayApplication.class, args);
    }
}

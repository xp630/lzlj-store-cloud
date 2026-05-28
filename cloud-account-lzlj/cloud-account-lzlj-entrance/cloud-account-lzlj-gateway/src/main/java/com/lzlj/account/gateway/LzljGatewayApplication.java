package com.lzlj.account.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * LZLJ Gateway 启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@SpringBootApplication
public class LzljGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LzljGatewayApplication.class, args);
    }
}

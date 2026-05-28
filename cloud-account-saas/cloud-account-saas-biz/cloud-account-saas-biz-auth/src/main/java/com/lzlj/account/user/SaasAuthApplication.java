package com.lzlj.account.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 用户服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@MapperScan({"com.lzlj.account.user.dao", "com.lzlj.account.tenant.dao", "com.lzlj.account.menu.dao", "com.lzlj.account.role.dao", "com.lzlj.account.openapi.dao", "com.lzlj.account.log.dao"})
@SpringBootApplication
@ComponentScan(basePackages = {"com.lzlj.account.*", "com.lzlj.account.*"})
public class SaasAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasAuthApplication.class, args);
    }
}

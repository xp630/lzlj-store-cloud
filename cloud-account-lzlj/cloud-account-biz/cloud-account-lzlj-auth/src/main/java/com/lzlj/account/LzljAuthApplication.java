package com.lzlj.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * LZLJ Auth 服务启动类
 */
@EnableDiscoveryClient
@EnableAsync
@MapperScan({"com.lzlj.account.user.mapper", "com.lzlj.account.menu.mapper", "com.lzlj.account.role.mapper", "com.lzlj.account.log.mapper"})
@SpringBootApplication
@ComponentScan(basePackages = {"com.lzlj.account", "com.lzlj.account"})
public class LzljAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(LzljAuthApplication.class, args);
    }
}

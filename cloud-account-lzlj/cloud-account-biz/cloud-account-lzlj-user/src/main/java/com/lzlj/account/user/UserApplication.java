package com.lzlj.account.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.lzlj.account.user.dao")
@SpringBootApplication
@ComponentScan(basePackages = {"com.lzlj.account.*", "com.lzlj.account.*"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

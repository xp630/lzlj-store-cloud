package com.lzlj.store.user;

import com.lzlj.store.common.core.config.RedissonConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 用户服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.lzlj.store.user.dao")
@SpringBootApplication
@ComponentScan(basePackages = {"com.lzlj.store.user", "com.lzlj.store.common.core"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedissonConfig.class))
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

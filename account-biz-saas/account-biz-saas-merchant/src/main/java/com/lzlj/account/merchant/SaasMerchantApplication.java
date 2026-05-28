package com.lzlj.account.merchant;

import com.lzlj.account.common.core.config.RedissonConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 商户服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@MapperScan({"com.lzlj.account.**.dao"})
@SpringBootApplication
@ComponentScan(basePackages = {"com.lzlj.account.*", "com.lzlj.account.*"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedissonConfig.class))
public class SaasMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasMerchantApplication.class, args);
    }
}

package com.lzlj.account.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SaaS 任务调度服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lzlj.account.common.api.feign")
@SpringBootApplication(scanBasePackages = {
    "com.lzlj.account"
}, exclude = {DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
public class SaasTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasTaskApplication.class, args);
    }
}

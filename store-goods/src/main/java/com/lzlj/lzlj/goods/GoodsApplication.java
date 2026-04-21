package com.lzlj.lzlj.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品服务启动类
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lzlj.store.common.api.feign")
@SpringBootApplication(scanBasePackages = {
    "com.lzlj.lzlj.goods",
    "com.lzlj.store.common.core"
}, exclude = {DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class})
public class GoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }
}

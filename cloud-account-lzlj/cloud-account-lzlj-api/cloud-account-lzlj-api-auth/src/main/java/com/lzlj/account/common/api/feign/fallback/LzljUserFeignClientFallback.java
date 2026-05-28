package com.lzlj.account.common.api.feign.fallback;

import com.lzlj.account.common.api.feign.LzljUserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LZLJ 用户服务 Feign 客户端降级处理
 */
@Slf4j
@Component
public class LzljUserFeignClientFallback implements LzljUserFeignClient {

    @Override
    public LzljUserFeignClient.LzljUserInfo getById(Long id) {
        log.warn("LZLJ UserFeignClient.getById 降级, id={}", id);
        return null;
    }

    @Override
    public LzljUserFeignClient.LzljUserInfo getCurrentUser() {
        log.warn("LZLJ UserFeignClient.getCurrentUser 降级");
        return null;
    }
}

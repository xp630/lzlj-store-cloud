package com.lzlj.account.user.handler;

import com.lzlj.account.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sentinel BlockHandler - Feign 调用降级处理
 * 当 saas-auth 调用其他服务失败时触发
 */
@Slf4j
@Component
public class FeignBlockHandler {

    /**
     * 统一降级处理
     */
    public static Result<Void> fallback(Throwable t) {
        log.warn("Sentinel降级[Feign调用]: 原因={}", t.getMessage());
        return Result.fail("服务暂时不可用，请稍后重试");
    }
}

package com.lzlj.account.goods.handler;

import com.lzlj.account.common.api.feign.UserFeignClient;
import com.lzlj.account.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Sentinel BlockHandler - Feign 调用降级处理
 * 注意：必须是 static 方法
 */
@Slf4j
@Component
public class UserFeignBlockHandler {

    // ==================== 统一降级策略（95%场景使用这个） ====================

    /**
     * 统一降级处理 - 所有 Feign 调用默认使用此降级策略
     * 使用方式: fallback = "unifiedFallback"
     */
    public static Result<UserFeignClient.UserInfo> unifiedFallback(Long id, Throwable t) {
        log.warn("Sentinel降级[统一策略]: id={}, 原因={}", id, t.getMessage());
        return Result.fail("服务暂时不可用，请稍后重试");
    }

    // ==================== 方法级别定制策略 ====================

    /**
     * getById 定制降级 - 可配置独立熔断规则
     * 使用方式: fallback = "getByIdBlockHandler"
     */
    public static Result<UserFeignClient.UserInfo> getByIdBlockHandler(Long id, Throwable t) {
        log.warn("Sentinel降级[getById]: id={}, 原因={}", id, t.getMessage());
        return Result.fail("获取用户信息失败，请稍后重试");
    }

    /**
     * getCurrentUser 定制降级 - 可配置独立熔断规则
     * 使用方式: fallback = "getCurrentUserBlockHandler"
     */
    public static Result<UserFeignClient.UserInfo> getCurrentUserBlockHandler(Throwable t) {
        log.warn("Sentinel降级[getCurrentUser]: 原因={}", t.getMessage());
        return Result.fail("获取当前用户信息失败，请稍后重试");
    }
}

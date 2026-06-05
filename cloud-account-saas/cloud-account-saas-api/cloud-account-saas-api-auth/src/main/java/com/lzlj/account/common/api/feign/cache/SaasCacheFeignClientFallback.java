package com.lzlj.account.common.api.feign.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SaaS 缓存刷新 Feign 客户端降级处理
 */
@Slf4j
@Component
public class SaasCacheFeignClientFallback implements SaasCacheFeignClient {

    @Override
    public Void refreshMenus() {
        log.warn("[Fallback] 刷新菜单缓存失败");
        return null;
    }

    @Override
    public Void refreshRoles() {
        log.warn("[Fallback] 刷新角色缓存失败");
        return null;
    }

    @Override
    public Void refreshDict(String dictType) {
        log.warn("[Fallback] 刷新数据字典缓存失败: dictType={}", dictType);
        return null;
    }

    @Override
    public Void refreshAllDict() {
        log.warn("[Fallback] 刷新所有数据字典缓存失败");
        return null;
    }
}

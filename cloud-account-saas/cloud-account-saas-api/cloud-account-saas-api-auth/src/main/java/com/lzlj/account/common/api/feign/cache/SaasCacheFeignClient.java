package com.lzlj.account.common.api.feign.cache;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * SaaS 缓存刷新 Feign 客户端
 */
@FeignClient(name = "saas-auth", contextId = "saas-cache", path = "/internal/cache", fallback = SaasCacheFeignClientFallback.class)
public interface SaasCacheFeignClient {

    /**
     * 刷新菜单缓存
     */
    @PostMapping("/menus/refresh")
    Void refreshMenus();

    /**
     * 刷新角色缓存
     */
    @PostMapping("/roles/refresh")
    Void refreshRoles();

    /**
     * 刷新指定类型的数据字典缓存
     */
    @PostMapping("/dict/{dictType}/refresh")
    Void refreshDict(@PathVariable("dictType") String dictType);

    /**
     * 刷新所有数据字典缓存
     */
    @PostMapping("/dict/all/refresh")
    Void refreshAllDict();
}

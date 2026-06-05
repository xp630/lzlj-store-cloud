package com.lzlj.account.cache.internal;

import com.lzlj.account.cache.SaasCacheService;
import com.lzlj.account.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部缓存刷新接口
 * <p>
 * 供 saas-task 调度服务调用，不对外暴露
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/cache")
@Tag(name = "内部接口-缓存管理")
public class InternalCacheController {

    private final SaasCacheService cacheService;

    @Operation(summary = "刷新菜单缓存")
    @PostMapping("/menus/refresh")
    public Result<Void> refreshMenus() {
        cacheService.invalidateMenus();
        log.info("[内部接口] 菜单缓存已失效");
        return Result.success();
    }

    @Operation(summary = "刷新角色缓存")
    @PostMapping("/roles/refresh")
    public Result<Void> refreshRoles() {
        cacheService.invalidateRoles();
        log.info("[内部接口] 角色缓存已失效");
        return Result.success();
    }

    @Operation(summary = "刷新指定类型的数据字典缓存")
    @PostMapping("/dict/{dictType}/refresh")
    public Result<Void> refreshDict(@PathVariable String dictType) {
        cacheService.invalidateDataDictionary(dictType);
        log.info("[内部接口] 数据字典缓存已失效: dictType={}", dictType);
        return Result.success();
    }

    @Operation(summary = "刷新所有数据字典缓存")
    @PostMapping("/dict/all/refresh")
    public Result<Void> refreshAllDict() {
        cacheService.invalidateDataDictionary(null);
        log.info("[内部接口] 所有数据字典缓存已失效");
        return Result.success();
    }
}

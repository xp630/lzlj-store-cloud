package com.lzlj.lzlj.goods.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.lzlj.lzlj.goods.handler.UserFeignBlockHandler;
import com.lzlj.lzlj.goods.service.GoodsService;
import com.lzlj.lzlj.goods.vo.GoodsVO;
import com.lzlj.account.common.api.feign.UserFeignClient;
import com.lzlj.account.common.api.feign.UserFeignClientForSentinel;
import com.lzlj.account.common.api.feign.fallback.UserFeignClientFallback;
import com.lzlj.account.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 演示：
 * 1. 本地服务调用 (GoodsService)
 * 2. 跨服务调用 (UserFeignClient 调用 store-user)
 */
@Slf4j
@Tag(name = "商品管理")
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;
    private final UserFeignClient userFeignClient;
    private final UserFeignClientForSentinel userFeignClientForSentinel;
    private final UserFeignClientFallback fallback;

    @Operation(summary = "获取商品列表 - 本地服务调用")
    @GetMapping("/list")
    public Result<List<GoodsVO>> list() {
        // 本地服务调用
        List<GoodsVO> goodsList = goodsService.list();
        return Result.success(goodsList);
    }

    @Operation(summary = "获取商品详情 - 本地服务调用")
    @GetMapping("/{id}")
    public Result<GoodsVO> getById(@PathVariable Long id) {
        // 本地服务调用
        GoodsVO goods = goodsService.getById(id);
        if (goods == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(goods);
    }

    @Operation(summary = "分页查询商品 - 本地服务调用")
    @GetMapping("/page")
    public Result<List<GoodsVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        // 本地服务调用
        List<GoodsVO> goodsList = goodsService.page(pageNum, pageSize);
        return Result.success(goodsList);
    }

    @Operation(summary = "创建商品 - 本地服务调用 + 跨服务调用")
    @PostMapping
    public Result<Long> create(@RequestBody GoodsVO goods) {
        // 跨服务调用 - 验证创建人是否存在
        log.info("跨服务调用: 验证用户 {}, userFeignClient={}", goods.getCreatorId(), userFeignClient);
        try {
            Result<UserFeignClient.UserInfo> userResult = userFeignClient.getById(goods.getCreatorId());
            if (userResult.getData() == null) {
                return Result.fail("创建人不存在");
            }
            log.info("跨服务调用成功: 用户信息={}", userResult.getData());
        } catch (Exception e) {
            log.warn("跨服务调用失败, 使用默认创建人: {}", e.getMessage());
        }

        // 本地服务调用 - 创建商品
        Long id = goodsService.create(goods);
        return Result.success(id);
    }

    @Operation(summary = "获取商品及创建人信息 - 本地服务调用 + 跨服务调用")
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getGoodsWithCreator(@PathVariable Long id) {
        // 本地服务调用
        GoodsVO goods = goodsService.getById(id);
        if (goods == null) {
            return Result.fail("商品不存在");
        }

        // 跨服务调用 - 获取创建人信息
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("goods", goods);

        try {
            Result<UserFeignClient.UserInfo> userResult = userFeignClient.getById(goods.getCreatorId());
            result.put("creator", userResult.getData());
            log.info("跨服务调用成功: 获取创建人信息成功, userId={}", goods.getCreatorId());
        } catch (Exception e) {
            log.warn("跨服务调用失败: {}", e.getMessage());
            result.put("creator", null);
        }

        return Result.success(result);
    }

    @Operation(summary = "测试Feign Fallback - 直接调用不过滤异常")
    @GetMapping("/test/feign/{id}")
    public Result<UserFeignClient.UserInfo> testFeignFallback(@PathVariable Long id) {
        log.info("测试Feign调用, id={}, userFeignClient.class={}", id, userFeignClient.getClass());
        log.info("userFeignClient 是代理对象吗？{}", java.lang.reflect.Proxy.isProxyClass(userFeignClient.getClass()));
        try {
            Result<UserFeignClient.UserInfo> result = userFeignClient.getById(id);
            log.info("Feign调用结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Feign调用抛出异常: type={}, message={}", e.getClass().getName(), e.getMessage());
            // 手动触发 Fallback 逻辑
            return fallback.getById(id);
        }
    }

    @Operation(summary = "测试Sentinel降级 - 使用@SentinelResource")
    @GetMapping("/test/sentinel/{id}")
    @SentinelResource(value = "userFeign#getById",
            fallbackClass = UserFeignBlockHandler.class,
            fallback = "getByIdBlockHandler")
    public Result<UserFeignClient.UserInfo> testSentinelFallback(@PathVariable Long id) {
        log.info("Sentinel测试: 调用userFeign.getById({})", id);
        return userFeignClient.getById(id);
    }

    // ==================== 统一降级策略示例 ====================

    @Operation(summary = "Sentinel统一降级 - 所有方法共用fallback策略")
    @GetMapping("/test/sentinel/unified/{id}")
    @SentinelResource(value = "userFeign#getById",
            fallbackClass = UserFeignBlockHandler.class,
            fallback = "fallback")
    public Result<UserFeignClient.UserInfo> testSentinelUnified(@PathVariable Long id) {
        return userFeignClient.getById(id);
    }

    @Operation(summary = "Sentinel统一降级 - getCurrentUser")
    @GetMapping("/test/sentinel/current")
    @SentinelResource(value = "userFeign#getCurrentUser",
            fallbackClass = UserFeignBlockHandler.class,
            fallback = "fallback")
    public Result<UserFeignClient.UserInfo> testSentinelCurrentUser() {
        return userFeignClient.getCurrentUser();
    }

    // ==================== Sentinel 真实降级测试（使用无 Fallback 的 Feign Client） ====================

    @Operation(summary = "Sentinel真实降级 - 使用无Fallback的Feign Client")
    @GetMapping("/test/sentinel/real/{id}")
    @SentinelResource(value = "userFeignReal#getById",
            fallbackClass = UserFeignBlockHandler.class,
            fallback = "getByIdBlockHandler")
    public Result<UserFeignClient.UserInfo> testSentinelRealFallback(@PathVariable Long id) {
        return userFeignClientForSentinel.getById(id);
    }

    @Operation(summary = "Sentinel真实降级 - 统一策略")
    @GetMapping("/test/sentinel/real/unified/{id}")
    @SentinelResource(value = "userFeignReal#getById",
            fallbackClass = UserFeignBlockHandler.class,
            fallback = "unifiedFallback")
    public Result<UserFeignClient.UserInfo> testSentinelRealUnified(@PathVariable Long id) {
        return userFeignClientForSentinel.getById(id);
    }
}

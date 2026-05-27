package com.lzlj.lzlj.goods.controller;

import com.lzlj.lzlj.goods.service.GoodsService;
import com.lzlj.lzlj.goods.vo.GoodsVO;
import com.lzlj.account.common.api.feign.UserFeignClient;
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

    @Operation(summary = "批量获取商品及创建人信息 - 本地服务调用 + 跨服务调用")
    @GetMapping("/batch/detail")
    public Result<Map<String, Object>> getBatchGoodsWithCreators(
            @RequestParam List<Long> ids) {
        List<GoodsVO> goodsList = new java.util.ArrayList<>();
        for (Long id : ids) {
            GoodsVO goods = goodsService.getById(id);
            if (goods != null) {
                goodsList.add(goods);
            }
        }

        // 跨服务调用 - 批量获取创建人信息
        String userIds = String.join(",", ids.stream().map(String::valueOf).collect(java.util.stream.Collectors.toList()));
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("goodsList", goodsList);

        try {
            Result<java.util.List<UserFeignClient.UserInfo>> userResult = userFeignClient.getBatchUsers(userIds);
            result.put("creators", userResult.getData());
            log.info("跨服务批量调用成功: 获取 {} 个用户信息", ids.size());
        } catch (Exception e) {
            log.warn("跨服务批量调用失败: {}", e.getMessage());
            result.put("creators", java.util.Collections.emptyList());
        }

        return Result.success(result);
    }
}

package com.lzlj.account.merchant.controller;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.merchant.dto.*;
import com.lzlj.account.merchant.service.LzljMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LZLJ 商户控制器
 */
@Tag(name = "商户管理")
@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class LzljMerchantController {

    private final LzljMerchantService merchantService;

    @Operation(summary = "网商同步商户")
    @PostMapping("/sync")
    public Result<MerchantDTO> syncMerchant(@RequestBody SyncMerchantDTO dto) {
        return Result.success(merchantService.syncFromWangshang(dto));
    }

    @Operation(summary = "商户分页列表")
    @GetMapping("/page")
    public Result<PageResult<MerchantDTO>> page(MerchantQueryDTO query) {
        return Result.success(merchantService.page(query));
    }

    @Operation(summary = "获取商户详情")
    @GetMapping("/{id}")
    public Result<MerchantDTO> getById(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    @Operation(summary = "创建商户")
    @PostMapping
    public Result<MerchantDTO> create(@RequestBody @Valid CreateMerchantDTO dto) {
        return Result.success(merchantService.create(dto));
    }

    @Operation(summary = "更新商户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateMerchantDTO dto) {
        merchantService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除商户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        merchantService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取结算信息")
    @GetMapping("/{id}/settlement")
    public Result<SettlementInfoDTO> getSettlement(@PathVariable Long id) {
        return Result.success(merchantService.getSettlement(id));
    }

    @Operation(summary = "更新结算信息")
    @PutMapping("/{id}/settlement")
    public Result<Void> updateSettlement(@PathVariable Long id, @RequestBody SettlementInfoDTO dto) {
        merchantService.updateSettlement(id, dto);
        return Result.success();
    }

    @Operation(summary = "获取商户账号列表")
    @GetMapping("/{id}/users")
    public Result<List<MerchantUserDTO>> getUsers(@PathVariable Long id) {
        return Result.success(merchantService.getUsers(id));
    }

    @Operation(summary = "关联用户到商户")
    @PostMapping("/{id}/users")
    public Result<Void> assignUser(@PathVariable Long id, @RequestBody @Valid AssignMerchantUserDTO dto) {
        merchantService.assignUser(id, dto);
        return Result.success();
    }

    @Operation(summary = "解绑商户用户")
    @DeleteMapping("/{id}/users/{userId}")
    public Result<Void> unbindUser(@PathVariable Long id, @PathVariable Long userId) {
        merchantService.unbindUser(id, userId);
        return Result.success();
    }
}

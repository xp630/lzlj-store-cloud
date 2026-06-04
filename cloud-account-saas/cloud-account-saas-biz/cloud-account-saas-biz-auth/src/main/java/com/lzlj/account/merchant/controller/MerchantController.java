package com.lzlj.account.merchant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.merchant.dto.CreateMerchantDTO;
import com.lzlj.account.merchant.dto.MerchantDTO;
import com.lzlj.account.merchant.dto.UpdateMerchantDTO;
import com.lzlj.account.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商户管理控制器
 */
@Tag(name = "商户管理")
@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @SaCheckPermission("saas:merchant:create")
    @Operation(summary = "创建商户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateMerchantDTO dto) {
        return Result.success(merchantService.create(dto));
    }

    @SaCheckPermission("saas:merchant:update")
    @Operation(summary = "更新商户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateMerchantDTO dto) {
        merchantService.update(id, dto);
        return Result.success();
    }

    @SaCheckPermission("saas:merchant:delete")
    @Operation(summary = "删除商户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        merchantService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("saas:merchant:list")
    @Operation(summary = "获取商户详情")
    @GetMapping("/{id}")
    public Result<MerchantDTO> getById(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    @SaCheckPermission("saas:merchant:list")
    @Operation(summary = "根据编码获取商户")
    @GetMapping("/code/{code}")
    public Result<MerchantDTO> getByCode(@PathVariable String code) {
        return Result.success(merchantService.getByCode(code));
    }

    @SaCheckPermission("saas:merchant:list")
    @Operation(summary = "分页查询商户")
    @GetMapping("/page")
    public Result<PageResult<MerchantDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(merchantService.page(keyword, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @SaCheckPermission("saas:merchant:update")
    @Operation(summary = "修改商户状态")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        merchantService.changeStatus(id, status);
        return Result.success();
    }
}

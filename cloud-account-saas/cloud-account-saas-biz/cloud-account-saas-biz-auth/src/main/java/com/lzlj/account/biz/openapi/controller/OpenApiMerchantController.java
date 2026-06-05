package com.lzlj.account.biz.openapi.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.merchant.dto.MerchantDTO;
import com.lzlj.account.biz.merchant.dto.MerchantQueryDTO;
import com.lzlj.account.biz.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * OpenAPI 商户控制器
 * 供外部系统（如 LZLJ）通过 OpenAPI 调用
 */
@Tag(name = "OpenAPI-商户管理")
@RestController
@RequestMapping("/openapi/merchant")
@RequiredArgsConstructor
public class OpenApiMerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "获取商户详情")
    @GetMapping("/{id}")
    public Result<MerchantDTO> getById(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    @Operation(summary = "根据编码获取商户")
    @GetMapping("/code/{code}")
    public Result<MerchantDTO> getByCode(@PathVariable String code) {
        return Result.success(merchantService.getByCode(code));
    }

    @Operation(summary = "分页查询商户")
    @PostMapping("/page")
    public Result<PageResult<MerchantDTO>> page(@RequestBody PageRequest<MerchantQueryDTO> pageRequest) {
        return Result.success(merchantService.page(pageRequest));
    }
}

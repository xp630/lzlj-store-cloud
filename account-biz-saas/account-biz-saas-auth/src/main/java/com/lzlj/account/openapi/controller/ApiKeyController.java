package com.lzlj.account.openapi.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.openapi.dto.ApiKeyAuthDTO;
import com.lzlj.account.openapi.dto.ApiKeyDTO;
import com.lzlj.account.openapi.dto.CreateApiKeyDTO;
import com.lzlj.account.openapi.dto.UpdateApiKeyDTO;
import com.lzlj.account.openapi.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * API密钥管理控制器
 */
@Tag(name = "API密钥管理")
@RestController
@RequestMapping("/openapi/key")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @Operation(summary = "创建API密钥")
    @PostMapping
    public Result<ApiKeyDTO> create(@Valid @RequestBody CreateApiKeyDTO dto) {
        return Result.success(apiKeyService.create(dto));
    }

    @Operation(summary = "更新API密钥")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateApiKeyDTO dto) {
        apiKeyService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除API密钥")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        apiKeyService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取API密钥详情")
    @GetMapping("/{id}")
    public Result<ApiKeyDTO> getById(@PathVariable Long id) {
        return Result.success(apiKeyService.getById(id));
    }

    @Operation(summary = "分页查询API密钥")
    @GetMapping("/page")
    public Result<PageResult<ApiKeyDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(apiKeyService.page(tenantId, keyword, status,
                pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "修改API密钥状态")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        apiKeyService.changeStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据API Key获取认证信息（内部接口，供网关调用）")
    @GetMapping("/auth/{apiKey}")
    public Result<ApiKeyAuthDTO> getAuthInfo(@PathVariable String apiKey) {
        return Result.success(apiKeyService.getAuthInfoByApiKey(apiKey));
    }
}

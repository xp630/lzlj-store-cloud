package com.lzlj.account.tenant.controller;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.tenant.dto.CreateTenantDTO;
import com.lzlj.account.tenant.dto.TenantDTO;
import com.lzlj.account.tenant.dto.UpdateTenantDTO;
import com.lzlj.account.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 租户管理控制器
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "创建租户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateTenantDTO dto) {
        return Result.success(tenantService.create(dto));
    }

    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateTenantDTO dto) {
        tenantService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取租户详情")
    @GetMapping("/{id}")
    public Result<TenantDTO> getById(@PathVariable Long id) {
        return Result.success(tenantService.getById(id));
    }

    @Operation(summary = "根据编码获取租户")
    @GetMapping("/code/{code}")
    public Result<TenantDTO> getByCode(@PathVariable String code) {
        return Result.success(tenantService.getByCode(code));
    }

    @Operation(summary = "分页查询租户")
    @GetMapping("/page")
    public Result<PageResult<TenantDTO>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(tenantService.page(keyword, status, pageNum, pageSize));
    }

    @Operation(summary = "修改租户状态")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        tenantService.changeStatus(id, status);
        return Result.success();
    }
}

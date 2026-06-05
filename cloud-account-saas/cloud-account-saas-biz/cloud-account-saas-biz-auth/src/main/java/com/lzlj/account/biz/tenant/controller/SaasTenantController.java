package com.lzlj.account.biz.tenant.controller;

import com.lzlj.account.biz.tenant.channel.TenantChannelDetailDTO;
import com.lzlj.account.biz.tenant.service.SaasTenantService;
import com.lzlj.account.biz.tenantchannel.service.SaasTenantChannelService;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.tenant.dto.CreateTenantDTO;
import com.lzlj.account.biz.tenant.dto.TenantDTO;
import com.lzlj.account.biz.tenant.dto.TenantQueryDTO;
import com.lzlj.account.biz.tenant.dto.UpdateTenantDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 租户管理控制器
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class SaasTenantController {

    private final SaasTenantService tenantService;
    private final SaasTenantChannelService tenantChannelService;

    @Operation(summary = "创建租户", description = "创建新租户，可同时配置开通的支付渠道及费率")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateTenantDTO dto) {
        return Result.success(tenantService.create(dto));
    }

    @Operation(summary = "更新租户", description = "更新租户信息，渠道配置会全量替换")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateTenantDTO dto) {
        tenantService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除租户", description = "删除租户及其关联的渠道配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取租户详情", description = "根据ID获取租户详情，包含渠道配置信息")
    @GetMapping("/{id}")
    public Result<TenantDTO> getById(@PathVariable Long id) {
        return Result.success(tenantService.getById(id));
    }

    @Operation(summary = "根据编码获取租户", description = "根据租户编码获取租户详情")
    @GetMapping("/code/{code}")
    public Result<TenantDTO> getByCode(@PathVariable String code) {
        return Result.success(tenantService.getByCode(code));
    }

    @Operation(summary = "分页查询租户", description = "支持多条件分页查询租户列表")
    @PostMapping("/page")
    public Result<PageResult<TenantDTO>> page(@RequestBody PageRequest<TenantQueryDTO> pageRequest) {
        return Result.success(tenantService.page(pageRequest.getCondition(), pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "修改租户状态", description = "启用或禁用租户")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long id,
            @RequestParam Integer status) {
        tenantService.changeStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取租户渠道详情", description = "获取指定租户的渠道详细信息，包含标准渠道信息和租户费率")
    @GetMapping("/{tenantId}/channels")
    public Result<List<TenantChannelDetailDTO>> getChannelDetails(@PathVariable Long tenantId) {
        return Result.success(tenantChannelService.getDetailByTenantId(tenantId));
    }

}

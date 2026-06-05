package com.lzlj.account.biz.tenant.controller;

import com.lzlj.account.biz.tenant.channel.TenantChannelDetailDTO;
import com.lzlj.account.biz.tenantchannel.dto.TenantChannelQueryDTO;
import com.lzlj.account.biz.tenantchannel.service.SaasTenantChannelService;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理控制器
 */
@Tag(name = "客户支付渠道管理")
@RestController
@RequestMapping("/channel/tenant")
@RequiredArgsConstructor
public class SaasTenantChannelController {

    private final SaasTenantChannelService tenantChannelService;

    @Operation(summary = "下游支付渠道分页查询", description = "多条件分页查询租户渠道信息，支持按租户名称(模糊)、租户ID、渠道ID、渠道编码、状态筛选")
    @PostMapping("/channels/page")
    public Result<PageResult<TenantChannelDetailDTO>> channelPage(@RequestBody PageRequest<TenantChannelQueryDTO> pageRequest) {
        return Result.success(tenantChannelService.page(
                pageRequest.getCondition(),
                pageRequest.getPageNum(),
                pageRequest.getPageSize()));
    }
}

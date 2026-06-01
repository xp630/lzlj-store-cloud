package com.lzlj.account.paymentchannel.controller;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelQueryDTO;
import com.lzlj.account.paymentchannel.service.LzljPaymentChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LZLJ 支付通道控制器
 */
@Tag(name = "LZLJ支付通道管理")
@RestController
@RequestMapping("/payment-channel")
@RequiredArgsConstructor
public class LzljPaymentChannelController {

    private final LzljPaymentChannelService paymentChannelService;

    @Operation(summary = "获取支付通道详情")
    @GetMapping("/{id}")
    public Result<LzljPaymentChannelDTO> getById(@PathVariable Long id) {
        return Result.success(paymentChannelService.getById(id));
    }

    @Operation(summary = "支付通道分页列表")
    @GetMapping("/page")
    public Result<PageResult<LzljPaymentChannelDTO>> page(LzljPaymentChannelQueryDTO query) {
        return Result.success(paymentChannelService.page(query));
    }

    @Operation(summary = "获取所有启用的支付通道")
    @GetMapping("/enabled")
    public Result<List<LzljPaymentChannelDTO>> listEnabled() {
        return Result.success(paymentChannelService.listEnabled());
    }

    @Operation(summary = "同步支付通道（从外部系统）")
    @PostMapping("/sync")
    public Result<Void> sync() {
        paymentChannelService.syncFromExternal();
        return Result.success();
    }
}

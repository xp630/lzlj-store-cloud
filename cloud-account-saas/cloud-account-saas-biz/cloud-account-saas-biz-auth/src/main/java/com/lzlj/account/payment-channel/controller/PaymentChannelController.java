package com.lzlj.account.paymentchannel.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.paymentchannel.dto.CreatePaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelQueryDTO;
import com.lzlj.account.paymentchannel.dto.UpdatePaymentChannelDTO;
import com.lzlj.account.paymentchannel.service.PaymentChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 支付通道管理控制器
 */
@Tag(name = "支付通道管理")
@RestController
@RequestMapping("/payment-channel")
@RequiredArgsConstructor
public class PaymentChannelController {

    private final PaymentChannelService paymentChannelService;

    @Operation(summary = "创建支付通道")
    @OperationLog(module = "payment-channel", operation = "CREATE", content = "创建支付通道")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreatePaymentChannelDTO dto) {
        return Result.success(paymentChannelService.create(dto));
    }

    @Operation(summary = "更新支付通道")
    @OperationLog(module = "payment-channel", operation = "UPDATE", content = "更新支付通道")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdatePaymentChannelDTO dto) {
        paymentChannelService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除支付通道")
    @OperationLog(module = "payment-channel", operation = "DELETE", content = "删除支付通道")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paymentChannelService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取支付通道详情")
    @GetMapping("/{id}")
    public Result<PaymentChannelDTO> getById(@PathVariable Long id) {
        return Result.success(paymentChannelService.getById(id));
    }

    @Operation(summary = "分页查询支付通道")
    @GetMapping("/page")
    public Result<PageResult<PaymentChannelDTO>> page(
            PageRequest pageRequest,
            PaymentChannelQueryDTO query) {
        return Result.success(paymentChannelService.page(query, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "获取支付通道列表")
    @GetMapping("/list")
    public Result<List<PaymentChannelDTO>> list() {
        return Result.success(paymentChannelService.list());
    }
}

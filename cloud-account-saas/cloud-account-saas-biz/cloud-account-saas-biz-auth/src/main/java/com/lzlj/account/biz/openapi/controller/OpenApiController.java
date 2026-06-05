package com.lzlj.account.biz.openapi.controller;

import com.lzlj.account.biz.openapi.dto.OpenApiIdRequest;
import com.lzlj.account.biz.openapi.dto.OpenApiMerchantCodeRequest;
import com.lzlj.account.biz.openapi.dto.OpenApiPaymentChannelListRequest;
import com.lzlj.account.biz.openapi.dto.OpenApiPaymentChannelPageRequest;
import com.lzlj.account.biz.paychannel.dto.PaymentChannelDTO;
import com.lzlj.account.biz.paychannel.service.SaasPaymentChannelService;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.merchant.dto.MerchantDTO;
import com.lzlj.account.biz.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * OpenAPI 统一控制器
 * 所有外部系统调用统一使用 POST + JSON Body
 */
@Tag(name = "OpenAPI统一接口")
@RestController
@RequestMapping("/openapi")
@RequiredArgsConstructor
public class OpenApiController {

    private final MerchantService merchantService;
    private final SaasPaymentChannelService paymentChannelService;

    // ==================== 商户接口 ====================

    @Operation(summary = "获取商户详情（ID）")
    @PostMapping("/merchant/getById")
    public Result<MerchantDTO> getMerchantById(@RequestBody @Valid OpenApiIdRequest request) {
        return Result.success(merchantService.getById(request.getId()));
    }

    @Operation(summary = "获取商户详情（编码）")
    @PostMapping("/merchant/getByCode")
    public Result<MerchantDTO> getMerchantByCode(@RequestBody @Valid OpenApiMerchantCodeRequest request) {
        return Result.success(merchantService.getByCode(request.getMerchantCode()));
    }

    // ==================== 支付通道接口 ====================

    @Operation(summary = "获取支付通道详情")
    @PostMapping("/paymentChannel/getById")
    public Result<PaymentChannelDTO> getPaymentChannelById(@RequestBody @Valid OpenApiIdRequest request) {
        return Result.success(paymentChannelService.getById(request.getId()));
    }

    @Operation(summary = "分页查询支付通道")
    @PostMapping("/paymentChannel/page")
    public Result<PageResult<PaymentChannelDTO>> pagePaymentChannel(@RequestBody @Valid OpenApiPaymentChannelPageRequest request) {
        return Result.success(paymentChannelService.page(
                request.toQueryDTO(),
                request.getPageNum(),
                request.getPageSize()));
    }

    @Operation(summary = "查询支付通道列表")
    @PostMapping("/paymentChannel/list")
    public Result<?> listPaymentChannel(@RequestBody @Valid OpenApiPaymentChannelListRequest request) {
        return Result.success(paymentChannelService.list());
    }
}

package com.lzlj.account.biz.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenAPI 支付通道列表请求
 */
@Data
@Schema(description = "支付通道列表请求")
public class OpenApiPaymentChannelListRequest {

    @Schema(description = "状态（0:禁用 1:启用，不传则查全部）")
    private Integer status;
}

package com.lzlj.account.paymentchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支付通道查询DTO
 */
@Data
@Schema(description = "支付通道查询参数")
public class PaymentChannelQueryDTO {

    @Schema(description = "通道名称（模糊搜索）")
    private String channelName;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}

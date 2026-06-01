package com.lzlj.account.paymentchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * LZLJ 支付通道DTO
 */
@Data
@Schema(description = "支付通道详情")
public class LzljPaymentChannelDTO {

    @Schema(description = "通道ID")
    private Long id;

    @Schema(description = "通道编码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "通道类型")
    private String channelType;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "费率")
    private BigDecimal feeRate;

    @Schema(description = "最低交易金额")
    private BigDecimal minAmount;

    @Schema(description = "最高交易金额")
    private BigDecimal maxAmount;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

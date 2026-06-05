package com.lzlj.account.biz.paychannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SaaS 支付通道DTO
 * 字段已迁移至 common-core 的 PaymentChannelDTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付通道响应")
public class PaymentChannelDTO extends com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO {
    // 字段已迁移至 common-core，此处仅用于兼容
}

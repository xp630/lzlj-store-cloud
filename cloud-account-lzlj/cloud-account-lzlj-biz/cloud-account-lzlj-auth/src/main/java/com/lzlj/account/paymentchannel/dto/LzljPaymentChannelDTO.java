package com.lzlj.account.paymentchannel.dto;

import com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 支付通道DTO（复用 common-core 统一 DTO）
 * @deprecated 直接使用 {@link PaymentChannelDTO}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付通道详情")
public class LzljPaymentChannelDTO extends PaymentChannelDTO {
    // 字段已迁移至 common-core 的 PaymentChannelDTO，此 DTO 仅用于兼容
}

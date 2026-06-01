package com.lzlj.account.paymentchannel.dto;

import com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 支付通道DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付通道详情")
public class LzljPaymentChannelDTO extends PaymentChannelDTO {

    @Schema(description = "通道类型")
    private String channelType;

    @Schema(description = "描述")
    private String description;
}

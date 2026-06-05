package com.lzlj.account.biz.merchantchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 开通商户渠道请求
 */
@Data
@Schema(description = "开通商户渠道请求")
public class CreateMerchantChannelDTO {

    @NotNull(message = "商户ID不能为空")
    @Schema(description = "商户ID")
    private Long merchantId;

    @NotNull(message = "渠道ID不能为空")
    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "费率值(如0.006表示0.6%)")
    private BigDecimal rateValue;
}

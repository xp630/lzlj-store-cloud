package com.lzlj.account.biz.merchantchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户渠道DTO
 */
@Data
@Schema(description = "商户渠道响应")
public class MerchantChannelDTO {

    private Long id;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "渠道编码")
    private String channelCode;

    @Schema(description = "渠道名称")
    private String channelName;

    @Schema(description = "开通状态(0关闭 1开通)")
    private Integer status;

    @Schema(description = "费率类型(1固定费率)")
    private Integer rateType;

    @Schema(description = "费率值")
    private BigDecimal rateValue;

    private LocalDateTime createTime;
}

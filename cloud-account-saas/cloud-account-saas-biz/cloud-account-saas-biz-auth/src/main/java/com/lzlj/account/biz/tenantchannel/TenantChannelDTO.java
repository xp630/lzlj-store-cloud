package com.lzlj.account.biz.tenantchannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租户渠道DTO
 */
@Data
@Schema(description = "租户渠道响应")
public class TenantChannelDTO {

    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

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

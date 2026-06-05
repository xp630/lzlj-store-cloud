package com.lzlj.account.biz.tenantchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户渠道查询DTO
 */
@Data
@Schema(description = "租户渠道查询条件")
public class TenantChannelQueryDTO {

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "租户名称（模糊查询）")
    private String tenantName;

    @Schema(description = "渠道ID")
    private Long channelId;

    @Schema(description = "渠道编码")
    private String channelCode;

    @Schema(description = "状态：0关闭 1开通")
    private Integer status;
}
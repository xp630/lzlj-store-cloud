package com.lzlj.account.biz.tenantchannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新租户渠道请求
 */
@Data
@Schema(description = "更新租户渠道请求")
public class UpdateTenantChannelDTO {

    @Schema(description = "开通状态(0关闭 1开通)")
    private Integer status;

    @Schema(description = "费率值(如0.006表示0.6%)")
    private BigDecimal rateValue;
}

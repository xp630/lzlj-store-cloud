package com.lzlj.account.biz.tenant.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租户渠道详情DTO - 包含标准渠道信息和租户费率
 */
@Data
@Schema(description = "租户渠道详情响应")
public class TenantChannelDetailDTO {

    @Schema(description = "租户渠道关系ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "渠道ID")
    private Long channelId;

    // ========== 标准渠道信息 ==========

    @Schema(description = "通道编码（UNIONPAY/银联, NETBANK/网商）")
    private String channelCode;

    @Schema(description = "通道名称（银联/网商）")
    private String channelName;

    @Schema(description = "支付方式（逗号分隔，如 WECHAT,ALIPAY）")
    private String paymentMethod;

    @Schema(description = "云账户管理费率")
    private BigDecimal cloudAccountFee;

    @Schema(description = "上游成本费率")
    private BigDecimal upstreamCostFee;

    @Schema(description = "总费率成本（技术服务费）")
    private BigDecimal totalFeeCost;

    @Schema(description = "单笔限额")
    private BigDecimal perTransactionLimit;

    // ========== 租户费率配置 ==========

    @Schema(description = "开通状态(0关闭 1开通)")
    private Integer status;

    @Schema(description = "费率类型(1固定费率)")
    private Integer rateType;

    @Schema(description = "租户费率值(如0.006表示0.6%)")
    private BigDecimal rateValue;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
package com.lzlj.account.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LZLJ 结算信息DTO
 */
@Data
@Schema(description = "结算信息")
public class SettlementInfoDTO {

    @Schema(description = "结算信息ID")
    private Long id;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "结算类型 1:对公 2:对私")
    private Integer settlementType;

    @Schema(description = "开户行名称")
    private String bankName;

    @Schema(description = "开户行支行")
    private String bankBranchName;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "开户名称")
    private String accountName;

    @Schema(description = "结算周期 T+N")
    private String settlementCycle;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}

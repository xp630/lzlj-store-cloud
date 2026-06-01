package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantBaseDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * LZLJ 商户详情DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商户详情")
public class MerchantDTO extends MerchantBaseDTO {

    @Schema(description = "开户状态 0未开户 1开户中 2已开户 3开户失败")
    private Integer accountStatus;

    @Schema(description = "商户类型 1:母户 2:子户")
    private Integer merchantType;

    @Schema(description = "母商户ID")
    private Long parentId;

    @Schema(description = "网商商户账号")
    private String wangshangAccount;

    @Schema(description = "业务场景代码列表（母户用）")
    private List<String> scenarioCodes;

    @Schema(description = "业务场景ID（子户用）")
    private Long scenarioId;

    @Schema(description = "机构ID（母户）")
    private Long orgId;

    @Schema(description = "法人信息")
    private MerchantLegalDTO legal;

    @Schema(description = "银联账户列表")
    private List<MerchantChannelAccountDTO> channelAccounts;
}

package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * LZLJ 创建商户DTO
 */
@Data
@Schema(description = "创建商户请求")
public class CreateMerchantDTO {

    @NotBlank(message = "商户名称不能为空")
    @Schema(description = "商户全称")
    private String merchantName;

    @Schema(description = "商户简称")
    private String shortName;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "省代码")
    private String provinceCode;

    @Schema(description = "市代码")
    private String cityCode;

    @Schema(description = "区代码")
    private String districtCode;

    @Schema(description = "详细地址")
    private String address;

    @NotNull(message = "商户类型不能为空")
    @Schema(description = "商户类型 1:母户 2:子户")
    private Integer merchantType;

    @Schema(description = "经营类型 1:个人 2:企业经营 3:个体经营")
    private Integer businessType;

    @Schema(description = "网商商户账号")
    private String wangshangAccount;

    @Schema(description = "母商户ID（子户时必填）")
    private Long parentId;

    @Schema(description = "开户状态 0:未开户 1:开户中 2:已开户 3:开户失败")
    private Integer accountStatus;

    @Schema(description = "业务场景代码列表（母户用）")
    private List<String> scenarioCodes;

    @Schema(description = "业务场景ID（子户时必填）")
    private Long scenarioId;

    @Schema(description = "法人信息")
    private MerchantLegalDTO legal;

    @Schema(description = "银联账户列表")
    private List<MerchantChannelAccountDTO> channelAccounts;
}

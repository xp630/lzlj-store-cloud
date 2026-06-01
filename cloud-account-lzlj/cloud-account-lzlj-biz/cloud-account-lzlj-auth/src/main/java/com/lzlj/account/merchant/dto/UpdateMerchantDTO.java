package com.lzlj.account.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * LZLJ 更新商户DTO
 */
@Data
@Schema(description = "更新商户请求")
public class UpdateMerchantDTO {

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

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "法人代表")
    private String legalPerson;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "商户类型 1:母户 2:子户")
    private Integer merchantType;

    @Schema(description = "网商商户账号")
    private String wangshangAccount;

    @Schema(description = "业务场景ID列表")
    private List<Long> scenarioIds;
}

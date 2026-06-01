package com.lzlj.account.common.core.domain.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户法人信息DTO（两平台共享）
 */
@Data
@Schema(description = "商户法人信息")
public class MerchantLegalDTO {

    @Schema(description = "营业执照号")
    private String licenseNo;

    @Schema(description = "法人代表")
    private String legalPerson;

    @Schema(description = "营业执照图片URL")
    private String licensePic;

    @Schema(description = "法人身份证号")
    private String legalIdCard;

    @Schema(description = "法人身份证图片URL")
    private String legalIdCardPic;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}

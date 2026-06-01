package com.lzlj.account.common.core.domain.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户基础DTO（两平台共享）
 */
@Data
@Schema(description = "商户基础信息")
public class MerchantBaseDTO {

    @Schema(description = "商户ID")
    private Long id;

    @Schema(description = "商户编号")
    private String merchantCode;

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

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

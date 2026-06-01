package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * LZLJ 商户同步DTO（网商系统同步请求）
 */
@Data
@Schema(description = "商户同步请求")
public class SyncMerchantDTO {

    @NotBlank(message = "商户编号不能为空")
    @Schema(description = "商户编号")
    private String merchantCode;

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

    @Schema(description = "商户类型 1:母户 2:子户")
    private Integer merchantType;

    @Schema(description = "网商商户账号")
    private String wangshangAccount;

    @Schema(description = "业务场景代码列表")
    private List<String> scenarioCodes;

    @Schema(description = "法人信息")
    private MerchantLegalDTO legal;

    @Schema(description = "银联账户列表")
    private List<MerchantChannelAccountDTO> channelAccounts;
}

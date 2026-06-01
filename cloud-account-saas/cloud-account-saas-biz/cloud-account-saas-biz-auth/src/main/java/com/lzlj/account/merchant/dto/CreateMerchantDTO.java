package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * SaaS 创建商户DTO
 */
@Data
@Schema(description = "创建商户请求")
public class CreateMerchantDTO {

    @NotBlank(message = "商户编码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "商户编码只能包含字母、数字和下划线")
    @Schema(description = "商户编码")
    private String merchantCode;

    @NotBlank(message = "商户名称不能为空")
    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "商户简称")
    private String shortName;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "联系地址")
    private String address;

    @Schema(description = "支付渠道账号")
    private String wangshangAccount;

    @Schema(description = "法人信息")
    private MerchantLegalDTO legal;

    @Schema(description = "银联账户列表")
    private List<MerchantChannelAccountDTO> channelAccounts;
}

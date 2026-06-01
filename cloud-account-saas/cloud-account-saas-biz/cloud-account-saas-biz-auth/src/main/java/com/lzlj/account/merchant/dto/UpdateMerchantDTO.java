package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * SaaS 更新商户DTO
 */
@Data
@Schema(description = "更新商户请求")
public class UpdateMerchantDTO {

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

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "法人信息")
    private MerchantLegalDTO legal;

    @Schema(description = "银联账户列表")
    private List<MerchantChannelAccountDTO> channelAccounts;
}

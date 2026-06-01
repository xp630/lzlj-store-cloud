package com.lzlj.account.common.core.domain.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户银联账户信息DTO（两平台共享）
 */
@Data
@Schema(description = "商户银联账户信息")
public class MerchantChannelAccountDTO {

    @Schema(description = "支付渠道ID")
    private Long channelId;

    @Schema(description = "支付渠道名称")
    private String channelName;

    @Schema(description = "银联账号")
    private String unionPayAccount;

    @Schema(description = "开户名称")
    private String accountName;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}

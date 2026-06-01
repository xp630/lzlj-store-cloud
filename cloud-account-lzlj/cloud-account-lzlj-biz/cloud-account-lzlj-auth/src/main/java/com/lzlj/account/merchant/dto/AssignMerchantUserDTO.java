package com.lzlj.account.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * LZLJ 分配商户账号DTO
 */
@Data
@Schema(description = "分配商户账号请求")
public class AssignMerchantUserDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotNull(message = "角色不能为空")
    @Schema(description = "角色：merchant_admin门店管理员/clerk店员/cashier收银员")
    private String role;
}

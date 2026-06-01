package com.lzlj.account.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LZLJ 商户账号DTO
 */
@Data
@Schema(description = "商户账号")
public class MerchantUserDTO {

    @Schema(description = "关联ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色：merchant_admin门店管理员/clerk店员/cashier收银员")
    private String role;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}

package com.lzlj.account.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 更新租户请求DTO
 */
@Data
@Schema(description = "更新租户请求")
public class UpdateTenantDTO {

    @NotBlank(message = "租户名称不能为空")
    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "租户描述")
    private String tenantDesc;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "套餐ID")
    private Long packageId;

    @Schema(description = "用户数量上限")
    private Integer userLimit;

    @Schema(description = "logo")
    private String logo;
}

package com.lzlj.account.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 分配管理员可管理租户请求DTO
 */
@Data
@Schema(description = "分配管理员可管理租户请求")
public class AssignTenantDTO {

    @NotEmpty(message = "租户ID列表不能为空")
    @Schema(description = "租户ID列表")
    private List<Long> tenantIds;
}

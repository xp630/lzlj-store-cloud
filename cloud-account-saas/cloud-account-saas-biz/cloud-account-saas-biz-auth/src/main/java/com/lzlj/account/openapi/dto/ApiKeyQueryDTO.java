package com.lzlj.account.openapi.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API密钥查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API密钥查询条件")
public class ApiKeyQueryDTO extends PageRequest {

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "关键字（模糊搜索）")
    private String keyword;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}

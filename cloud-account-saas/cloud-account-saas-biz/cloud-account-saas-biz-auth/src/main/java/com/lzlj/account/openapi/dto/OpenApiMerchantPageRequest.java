package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenAPI 商户分页请求
 */
@Data
@Schema(description = "商户分页请求")
public class OpenApiMerchantPageRequest {

    @Schema(description = "关键字（模糊搜索商户名称或编码）")
    private String keyword;

    @Schema(description = "商户状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "页码", required = true)
    private Integer pageNum = 1;

    @Schema(description = "每页数量", required = true)
    private Integer pageSize = 10;
}

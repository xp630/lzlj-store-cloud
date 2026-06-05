package com.lzlj.account.biz.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * OpenAPI ID请求
 */
@Data
@Schema(description = "ID请求")
public class OpenApiIdRequest {

    @NotNull(message = "ID不能为空")
    @Schema(description = "ID", required = true)
    private Long id;
}

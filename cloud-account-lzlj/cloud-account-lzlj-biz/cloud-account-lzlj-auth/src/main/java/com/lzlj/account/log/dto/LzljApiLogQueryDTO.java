package com.lzlj.account.log.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ API访问日志查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ API访问日志查询条件")
public class LzljApiLogQueryDTO extends PageRequest {

    @Schema(description = "API密钥ID")
    private Long apiKeyId;

    @Schema(description = "请求路径（模糊搜索）")
    private String path;

    @Schema(description = "HTTP状态码")
    private Integer statusCode;
}

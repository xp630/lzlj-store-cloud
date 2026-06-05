package com.lzlj.account.log.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ操作日志查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ操作日志查询条件")
public class LzljOperationLogQueryDTO extends PageRequest {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "模块（模糊搜索）")
    private String module;

    @Schema(description = "操作类型")
    private String operation;
}

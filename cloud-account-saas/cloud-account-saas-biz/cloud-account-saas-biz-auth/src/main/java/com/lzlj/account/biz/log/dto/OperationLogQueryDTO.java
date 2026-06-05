package com.lzlj.account.biz.log.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志查询条件")
public class OperationLogQueryDTO extends PageRequest {

    @Schema(description = "用户名（模糊搜索）")
    private String username;

    @Schema(description = "模块（模糊搜索）")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;
}

package com.lzlj.account.biz.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志DTO
 */
@Data
@Schema(description = "操作日志")
public class OperationLogDTO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "操作人")
    private String username;

    @Schema(description = "模块")
    private String module;

    @Schema(description = "操作类型")
    private String operation;

    @Schema(description = "操作内容")
    private String content;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "浏览器UA")
    private String userAgent;

    @Schema(description = "操作人角色")
    private String roles;

    @Schema(description = "操作时间")
    private LocalDateTime createTime;
}

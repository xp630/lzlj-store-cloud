package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数响应DTO
 */
@Data
@Schema(description = "系统参数响应")
public class SystemParameterDTO {

    private Long id;

    /**
     * 参数编码
     */
    private String paramKey;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

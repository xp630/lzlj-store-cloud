package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "LZLJ系统参数响应")
public class LzljSystemParameterDTO {
    private Long id;
    private String paramKey;
    private String paramName;
    private String paramValue;
    private String paramType;
    private Integer status;
    private LocalDateTime createTime;
}

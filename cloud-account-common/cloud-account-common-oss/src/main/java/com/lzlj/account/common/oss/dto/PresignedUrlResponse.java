package com.lzlj.account.common.oss.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 预签名URL响应
 */
@Data
@Schema(description = "预签名URL响应")
public class PresignedUrlResponse {

    @Schema(description = "上传地址（PUT请求）")
    private String uploadUrl;

    @Schema(description = "文件访问URL")
    private String fileUrl;

    @Schema(description = "过期时间（秒）")
    private Integer expireSeconds;

    @Schema(description = "OSS对象名称")
    private String objectName;
}

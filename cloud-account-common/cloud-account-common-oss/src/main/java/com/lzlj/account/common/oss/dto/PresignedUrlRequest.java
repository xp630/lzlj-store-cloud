package com.lzlj.account.common.oss.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 预签名URL请求
 */
@Data
@Schema(description = "预签名URL请求")
public class PresignedUrlRequest {

    @NotBlank(message = "文件名不能为空")
    @Schema(description = "原始文件名")
    private String filename;

    @NotBlank(message = "Content-Type不能为空")
    @Schema(description = "文件MIME类型")
    private String contentType;

    @NotNull(message = "文件大小不能为空")
    @Schema(description = "文件大小（字节）")
    private Long size;

    @Schema(description = "文件类型（avatar/file/goods）", example = "avatar")
    private String type = "file";
}

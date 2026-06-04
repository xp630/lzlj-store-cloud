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

    @Schema(description = "业务类型：avatar=头像, license=营业执照, id_card=身份证, icon=图标,other=其他", example = "avatar")
    private String bizType = "other";
}

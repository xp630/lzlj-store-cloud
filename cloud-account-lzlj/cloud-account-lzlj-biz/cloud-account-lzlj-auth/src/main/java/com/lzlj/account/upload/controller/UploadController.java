package com.lzlj.account.upload.controller;

import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.common.oss.dto.PresignedUrlRequest;
import com.lzlj.account.common.oss.dto.PresignedUrlResponse;
import com.lzlj.account.common.oss.facade.OssUploadFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final OssUploadFacade ossUploadFacade;

    @Operation(summary = "获取预签名上传URL")
    @PostMapping("/presigned-url")
    public Result<PresignedUrlResponse> getPresignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        Long userId = UserContext.getUserId() != null ? UserContext.getUserId() : 0L;
        return ossUploadFacade.generateUploadUrl(request, userId, 0L);
    }
}

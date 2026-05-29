package com.lzlj.account.common.oss.facade;

import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.common.oss.dto.PresignedUrlRequest;
import com.lzlj.account.common.oss.dto.PresignedUrlResponse;
import com.lzlj.account.common.oss.service.OssUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * OSS 上传门面类
 * 始终注入，底层 OssUploadService 可选（未配置OSS时为空）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OssUploadFacade {

    private final Optional<OssUploadService> ossUploadService;

    /**
     * 生成预签名上传URL
     *
     * @param request  请求参数
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 预签名URL响应
     */
    public Result<PresignedUrlResponse> generateUploadUrl(PresignedUrlRequest request, Long userId, Long tenantId) {
        return ossUploadService
                .map(service -> {
                    PresignedUrlResponse response = service.generateUploadUrl(request, userId, tenantId);
                    return Result.success(response);
                })
                .orElseGet(() -> {
                    log.warn("OSS未配置，无法生成预签名URL");
                    return Result.fail("OSS未配置");
                });
    }
}

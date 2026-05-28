package com.lzlj.account.log.controller;

import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.log.dto.ApiAccessLogDTO;
import com.lzlj.account.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API日志控制器
 */
@Tag(name = "API日志管理")
@RestController
@RequestMapping("/inner/log")
@RequiredArgsConstructor
public class ApiLogController {

    private final LogService logService;

    @Operation(summary = "记录API访问日志（内部接口，供网关调用）")
    @PostMapping("/api")
    public Result<Void> logApiAccess(@RequestBody ApiAccessLogDTO dto) {
        logService.logApiAccess(
                dto.getApiKeyId(),
                dto.getApiKey(),
                dto.getTenantId(),
                dto.getMethod(),
                dto.getPath(),
                dto.getRequestBody(),
                dto.getResponseBody(),
                dto.getStatusCode(),
                dto.getDuration(),
                dto.getIp(),
                dto.getUserAgent(),
                dto.getErrorMsg()
        );
        return Result.success();
    }
}

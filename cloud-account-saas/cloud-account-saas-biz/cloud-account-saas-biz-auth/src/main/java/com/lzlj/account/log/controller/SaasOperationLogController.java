package com.lzlj.account.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.log.dto.OperationLogDTO;
import com.lzlj.account.log.dto.OperationLogQueryDTO;
import com.lzlj.account.log.service.SaasLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/log/operation")
@RequiredArgsConstructor
public class SaasOperationLogController {

    private final SaasLogService logService;

    @SaCheckPermission("saas:log:list")
    @Operation(summary = "分页查询操作日志", description = "支持按用户名、模块、操作类型搜索，按时间倒序")
    @GetMapping("/page")
    public Result<PageResult<OperationLogDTO>> page(OperationLogQueryDTO query) {
        return Result.success(logService.pageOperationLog(query));
    }
}

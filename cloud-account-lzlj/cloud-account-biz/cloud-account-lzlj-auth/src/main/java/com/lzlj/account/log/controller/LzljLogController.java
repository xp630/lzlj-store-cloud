package com.lzlj.account.log.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.log.entity.LzljApiLog;
import com.lzlj.account.log.entity.LzljOperationLog;
import com.lzlj.account.log.service.LzljLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LZLJ 日志控制器
 */
@Tag(name = "LZLJ日志管理")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LzljLogController {

    private final LzljLogService logService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/operation/page")
    public Result<PageResult<LzljOperationLog>> pageOperationLog(
            PageRequest pageRequest,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation) {
        Page<LzljOperationLog> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<LzljOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, LzljOperationLog::getUserId, userId)
               .like(module != null, LzljOperationLog::getModule, module)
               .like(operation != null, LzljOperationLog::getOperation, operation)
               .eq(LzljOperationLog::getDeleted, 0)
               .orderByDesc(LzljOperationLog::getCreateTime);

        IPage<LzljOperationLog> resultPage = logService.pageOperationLog(page, wrapper);

        return Result.success(new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        ));
    }

    @Operation(summary = "分页查询API访问日志")
    @GetMapping("/api/page")
    public Result<PageResult<LzljApiLog>> pageApiLog(
            PageRequest pageRequest,
            @RequestParam(required = false) Long apiKeyId,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) Integer statusCode) {
        Page<LzljApiLog> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<LzljApiLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(apiKeyId != null, LzljApiLog::getApiKeyId, apiKeyId)
               .like(path != null, LzljApiLog::getPath, path)
               .eq(statusCode != null, LzljApiLog::getStatusCode, statusCode)
               .eq(LzljApiLog::getDeleted, 0)
               .orderByDesc(LzljApiLog::getCreateTime);

        IPage<LzljApiLog> resultPage = logService.pageApiLog(page, wrapper);

        return Result.success(new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        ));
    }

    @Operation(summary = "获取操作日志详情")
    @GetMapping("/operation/{id}")
    public Result<LzljOperationLog> getOperationLog(@PathVariable Long id) {
        return Result.success(logService.getOperationLogById(id));
    }

    @Operation(summary = "获取API访问日志详情")
    @GetMapping("/api/{id}")
    public Result<LzljApiLog> getApiLog(@PathVariable Long id) {
        return Result.success(logService.getApiLogById(id));
    }
}

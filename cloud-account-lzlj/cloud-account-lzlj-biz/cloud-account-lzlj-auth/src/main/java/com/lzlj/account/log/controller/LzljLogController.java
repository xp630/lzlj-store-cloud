package com.lzlj.account.log.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.log.dto.LzljApiLogQueryDTO;
import com.lzlj.account.log.dto.LzljOperationLogQueryDTO;
import com.lzlj.account.log.entity.LzljApiLog;
import com.lzlj.account.log.entity.LzljOperationLog;
import com.lzlj.account.log.service.LzljLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    @PostMapping("/operation/page")
    public Result<PageResult<LzljOperationLog>> pageOperationLog(@RequestBody PageRequest<LzljOperationLogQueryDTO> pageRequest) {
        IPage<LzljOperationLog> resultPage = logService.pageOperationLog(pageRequest);
        return Result.success(new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        ));
    }

    @Operation(summary = "分页查询API访问日志")
    @PostMapping("/api/page")
    public Result<PageResult<LzljApiLog>> pageApiLog(@RequestBody PageRequest<LzljApiLogQueryDTO> pageRequest) {
        IPage<LzljApiLog> resultPage = logService.pageApiLog(pageRequest);
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

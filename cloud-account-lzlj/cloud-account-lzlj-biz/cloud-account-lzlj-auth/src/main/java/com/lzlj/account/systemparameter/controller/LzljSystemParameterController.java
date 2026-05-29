package com.lzlj.account.systemparameter.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.systemparameter.dto.*;
import com.lzlj.account.systemparameter.service.LzljSystemParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "LZLJ系统参数管理")
@RestController
@RequestMapping("/parameter")
@RequiredArgsConstructor
public class LzljSystemParameterController {

    private final LzljSystemParameterService lzljSystemParameterService;

    @Operation(summary = "创建系统参数")
    @OperationLog(module = "parameter", operation = "CREATE", content = "创建系统参数")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateLzljSystemParameterDTO dto) {
        return Result.success(lzljSystemParameterService.create(dto));
    }

    @Operation(summary = "更新系统参数")
    @OperationLog(module = "parameter", operation = "UPDATE", content = "更新系统参数")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateLzljSystemParameterDTO dto) {
        lzljSystemParameterService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除系统参数")
    @OperationLog(module = "929parameter", operation = "DELETE", content = "删除系统参数")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        lzljSystemParameterService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取系统参数详情")
    @GetMapping("/{id}")
    public Result<LzljSystemParameterDTO> getById(@PathVariable Long id) {
        return Result.success(lzljSystemParameterService.getById(id));
    }

    @Operation(summary = "根据key获取系统参数")
    @GetMapping("/key/{key}")
    public Result<LzljSystemParameterDTO> getByKey(@PathVariable String key) {
        return Result.success(lzljSystemParameterService.getByKey(key));
    }

    @Operation(summary = "分页查询系统参数")
    @GetMapping("/page")
    public Result<PageResult<LzljSystemParameterDTO>> page(
            PageRequest pageRequest,
            LzljSystemParameterQueryDTO query) {
        return Result.success(lzljSystemParameterService.page(query, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "获取系统参数列表")
    @GetMapping("/list")
    public Result<List<LzljSystemParameterDTO>> list() {
        return Result.success(lzljSystemParameterService.list());
    }
}

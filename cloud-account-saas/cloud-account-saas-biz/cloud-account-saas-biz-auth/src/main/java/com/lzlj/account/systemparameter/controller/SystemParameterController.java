package com.lzlj.account.systemparameter.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.systemparameter.dto.CreateSystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterQueryDTO;
import com.lzlj.account.systemparameter.dto.UpdateSystemParameterDTO;
import com.lzlj.account.systemparameter.service.SystemParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统参数管理控制器
 */
@Tag(name = "系统参数管理")
@RestController
@RequestMapping("/parameter")
@RequiredArgsConstructor
public class SystemParameterController {

    private final SystemParameterService systemParameterService;

    @Operation(summary = "创建系统参数")
    @OperationLog(module = "parameter", operation = "CREATE", content = "创建系统参数")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateSystemParameterDTO dto) {
        return Result.success(systemParameterService.create(dto));
    }

    @Operation(summary = "更新系统参数")
    @OperationLog(module = "parameter", operation = "UPDATE", content = "更新系统参数")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateSystemParameterDTO dto) {
        systemParameterService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除系统参数")
    @OperationLog(module = "parameter", operation = "DELETE", content = "删除系统参数")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        systemParameterService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取系统参数详情")
    @GetMapping("/{id}")
    public Result<SystemParameterDTO> getById(@PathVariable Long id) {
        return Result.success(systemParameterService.getById(id));
    }

    @Operation(summary = "根据key获取系统参数")
    @GetMapping("/key/{key}")
    public Result<SystemParameterDTO> getByKey(@PathVariable String key) {
        return Result.success(systemParameterService.getByKey(key));
    }

    @Operation(summary = "分页查询系统参数")
    @GetMapping("/page")
    public Result<PageResult<SystemParameterDTO>> page(
            PageRequest pageRequest,
            SystemParameterQueryDTO query) {
        return Result.success(systemParameterService.page(query, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "获取系统参数列表")
    @GetMapping("/list")
    public Result<List<SystemParameterDTO>> list() {
        return Result.success(systemParameterService.list());
    }
}

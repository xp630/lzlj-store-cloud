package com.lzlj.account.datadictionary.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.datadictionary.dto.*;
import com.lzlj.account.datadictionary.service.LzljDataDictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "LZLJ数据字典管理")
@RestController
@RequestMapping("/datadictionary")
@RequiredArgsConstructor
public class LzljDataDictionaryController {

    private final LzljDataDictionaryService lzljDataDictionaryService;

    @Operation(summary = "创建数据字典")
    @OperationLog(module = "dictionary", operation = "CREATE", content = "创建数据字典")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateLzljDataDictionaryDTO dto) {
        return Result.success(lzljDataDictionaryService.create(dto));
    }

    @Operation(summary = "更新数据字典")
    @OperationLog(module = "dictionary", operation = "UPDATE", content = "更新数据字典")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateLzljDataDictionaryDTO dto) {
        lzljDataDictionaryService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除数据字典")
    @OperationLog(module = "dictionary", operation = "DELETE", content = "删除数据字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        lzljDataDictionaryService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取数据字典详情")
    @GetMapping("/{id}")
    public Result<LzljDataDictionaryDTO> getById(@PathVariable Long id) {
        return Result.success(lzljDataDictionaryService.getById(id));
    }

    @Operation(summary = "分页查询数据字典")
    @GetMapping("/page")
    public Result<PageResult<LzljDataDictionaryDTO>> page(
            PageRequest pageRequest,
            LzljDataDictionaryQueryDTO query) {
        return Result.success(lzljDataDictionaryService.page(query, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "获取数据字典列表")
    @GetMapping("/list")
    public Result<List<LzljDataDictionaryDTO>> list() {
        return Result.success(lzljDataDictionaryService.list());
    }

    @Operation(summary = "根据类型获取数据字典")
    @GetMapping("/type/{type}")
    public Result<List<LzljDataDictionaryDTO>> getByType(@PathVariable String type) {
        return Result.success(lzljDataDictionaryService.getByType(type));
    }

    @Operation(summary = "获取所有字典类型分组")
    @GetMapping("/all-group")
    public Result<Map<String, List<LzljDataDictionaryDTO>>> getAllGroup() {
        return Result.success(lzljDataDictionaryService.getAllGroup());
    }
}

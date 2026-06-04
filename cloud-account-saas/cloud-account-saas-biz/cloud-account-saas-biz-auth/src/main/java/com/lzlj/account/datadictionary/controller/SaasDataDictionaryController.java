package com.lzlj.account.datadictionary.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.datadictionary.dto.CreateDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.DataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.DataDictionaryQueryDTO;
import com.lzlj.account.datadictionary.dto.UpdateDataDictionaryDTO;
import com.lzlj.account.datadictionary.service.DataDictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 数据字典管理控制器
 */
@Tag(name = "数据字典管理")
@RestController
@RequestMapping("/dictionary")
@RequiredArgsConstructor
public class SaasDataDictionaryController {

    private final DataDictionaryService dataDictionaryService;

    @Operation(summary = "创建数据字典")
    @OperationLog(module = "dictionary", operation = "CREATE", content = "创建数据字典")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateDataDictionaryDTO dto) {
        return Result.success(dataDictionaryService.create(dto));
    }

    @Operation(summary = "更新数据字典")
    @OperationLog(module = "dictionary", operation = "UPDATE", content = "更新数据字典")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateDataDictionaryDTO dto) {
        dataDictionaryService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除数据字典")
    @OperationLog(module = "dictionary", operation = "DELETE", content = "删除数据字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataDictionaryService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取数据字典详情")
    @GetMapping("/{id}")
    public Result<DataDictionaryDTO> getById(@PathVariable Long id) {
        return Result.success(dataDictionaryService.getById(id));
    }

    @Operation(summary = "分页查询数据字典")
    @GetMapping("/page")
    public Result<PageResult<DataDictionaryDTO>> page(
            PageRequest pageRequest,
            DataDictionaryQueryDTO query) {
        return Result.success(dataDictionaryService.page(query, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "根据类型获取数据字典")
    @GetMapping("/type/{type}")
    public Result<List<DataDictionaryDTO>> getByType(@PathVariable String type) {
        return Result.success(dataDictionaryService.getByType(type));
    }

    @Operation(summary = "获取所有字典类型分组")
    @GetMapping("/all-group")
    public Result<Map<String, List<DataDictionaryDTO>>> getAllGroup() {
        return Result.success(dataDictionaryService.getAllGroup());
    }

    @Operation(summary = "获取所有字典类型列表（去重，同一类型只显示一条）")
    @GetMapping("/types")
    public Result<List<DataDictionaryDTO>> getTypes() {
        return Result.success(dataDictionaryService.getTypes());
    }
}

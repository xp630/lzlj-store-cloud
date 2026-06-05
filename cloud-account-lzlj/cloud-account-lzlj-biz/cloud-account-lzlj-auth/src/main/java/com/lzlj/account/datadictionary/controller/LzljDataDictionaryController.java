package com.lzlj.account.datadictionary.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.enums.ModuleEnum;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryDTO;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryQueryDTO;
import com.lzlj.account.common.core.domain.datadictionary.SaveDataDictionaryDTO;
import com.lzlj.account.common.core.result.Result;
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
@RequestMapping("/dictionary")
@RequiredArgsConstructor
public class LzljDataDictionaryController {

    private final LzljDataDictionaryService lzljDataDictionaryService;

    @Operation(summary = "批量保存数据字典（创建/更新）")
    @OperationLog(module = ModuleEnum.DICTIONARY, operation = "SAVE", content = "批量保存数据字典")
    @PostMapping("/batch")
    public Result<Void> saveBatch(@Valid @RequestBody List<SaveDataDictionaryDTO> dtos) {
        lzljDataDictionaryService.saveBatch(dtos);
        return Result.success();
    }
    @Operation(summary = "删除数据字典")
    @OperationLog(module = ModuleEnum.DICTIONARY, operation = "DELETE", content = "删除数据字典")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        lzljDataDictionaryService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取数据字典详情")
    @GetMapping("/{id}")
    public Result<DataDictionaryDTO> getById(@PathVariable Long id) {
        return Result.success(lzljDataDictionaryService.getById(id));
    }

    @Operation(summary = "获取数据字典列表")
    @GetMapping("/list")
    public Result<List<DataDictionaryDTO>> list() {
        return Result.success(lzljDataDictionaryService.list());
    }

    @Operation(summary = "根据类型获取数据字典")
    @GetMapping("/type/{type}")
    public Result<List<DataDictionaryDTO>> getByType(@PathVariable String type) {
        return Result.success(lzljDataDictionaryService.getByType(type));
    }

    @Operation(summary = "获取所有字典类型分组")
    @GetMapping("/allGroup")
    public Result<Map<String, List<DataDictionaryDTO>>> getAllGroup() {
        return Result.success(lzljDataDictionaryService.getAllGroup());
    }

    @Operation(summary = "获取所有字典类型列表（去重，同一类型只显示一条）")
    @PostMapping("/types")
    public Result<PageResult<DataDictionaryDTO>> getTypesPage(@RequestBody PageRequest<DataDictionaryQueryDTO> pageRequest) {
        return Result.success(lzljDataDictionaryService.getPage(pageRequest.getCondition(), pageRequest.getPageNum(), pageRequest.getPageSize()));
    }
}

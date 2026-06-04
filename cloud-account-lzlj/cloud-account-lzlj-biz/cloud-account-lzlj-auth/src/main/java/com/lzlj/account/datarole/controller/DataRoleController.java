package com.lzlj.account.datarole.controller;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.datarole.dto.*;
import com.lzlj.account.datarole.entity.DataRoleCondition;
import com.lzlj.account.datarole.service.DataRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

/**
 * 数据角色控制器
 */
@Tag(name = "数据角色管理")
@RestController
@RequestMapping("/dataRole")
@RequiredArgsConstructor
public class DataRoleController {

    private final DataRoleService dataRoleService;

    @Operation(summary = "创建数据角色")
    @PostMapping
    public Result<DataRoleDTO> create(@RequestBody @Valid CreateDataRoleDTO dto) {
        return Result.success(dataRoleService.create(dto));
    }

    @Operation(summary = "更新数据角色")
    @PutMapping("/{id}")
    public Result<DataRoleDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateDataRoleDTO dto) {
        return Result.success(dataRoleService.update(id, dto));
    }

    @Operation(summary = "删除数据角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataRoleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取数据角色详情")
    @GetMapping("/{id}")
    public Result<DataRoleDTO> getById(@PathVariable Long id) {
        return Result.success(dataRoleService.getById(id));
    }

    @Operation(summary = "分页查询数据角色")
    @GetMapping("/page")
    public Result<PageResult<DataRoleDTO>> page(DataRoleQueryDTO query) {
        return Result.success(dataRoleService.page(query));
    }

    @Operation(summary = "分配数据角色给用户")
    @PutMapping("/assign")
    public Result<Void> assign(@RequestBody @Valid AssignDataRoleDTO dto) {
        dataRoleService.assign(dto);
        return Result.success();
    }

    @Operation(summary = "获取用户的数据角色列表")
    @GetMapping("/user/{userId}")
    public Result<List<DataRoleDTO>> getUserDataRoles(@PathVariable Long userId) {
        return Result.success(dataRoleService.getUserDataRoles(userId));
    }

    @Operation(summary = "获取当前用户的数据权限条件")
    @GetMapping("/currentUserConditions")
    public Result<List<DataRoleCondition>> getCurrentUserConditions() {
        return Result.success(dataRoleService.getCurrentUserDataRoleConditions());
    }
}

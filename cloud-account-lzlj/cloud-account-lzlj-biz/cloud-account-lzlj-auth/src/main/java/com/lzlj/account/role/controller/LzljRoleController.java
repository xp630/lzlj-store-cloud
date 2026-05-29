package com.lzlj.account.role.controller;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.role.dto.LzljCreateRoleDTO;
import com.lzlj.account.role.dto.LzljRoleDTO;
import com.lzlj.account.role.dto.LzljRoleMenuDTO;
import com.lzlj.account.role.dto.LzljUpdateRoleDTO;
import com.lzlj.account.role.service.LzljRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * LZLJ 角色管理控制器
 */
@Tag(name = "LZLJ角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class LzljRoleController {

    private final LzljRoleService roleService;

    @Operation(summary = "创建角色")
    @OperationLog(module = "role", operation = "CREATE", content = "创建角色")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody LzljCreateRoleDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @Operation(summary = "更新角色")
    @OperationLog(module = "role", operation = "UPDATE", content = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LzljUpdateRoleDTO dto) {
        roleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @OperationLog(module = "role", operation = "DELETE", content = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<LzljRoleDTO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<LzljRoleDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(roleService.page(keyword, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "获取角色已授权菜单")
    @GetMapping("/{id}/menus")
    public Result<List<LzljMenuDTO>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenus(id));
    }

    @Operation(summary = "获取角色已授权菜单（树形）")
    @GetMapping("/{id}/menus/tree")
    public Result<List<LzljMenuDTO>> getRoleMenusTree(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenusTree(id));
    }

    @Operation(summary = "分配菜单权限")
    @OperationLog(module = "role", operation = "GRANT", content = "分配菜单权限")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody LzljRoleMenuDTO dto) {
        roleService.assignMenus(id, dto);
        return Result.success();
    }
}

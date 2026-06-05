package com.lzlj.account.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.role.dto.LzljCreateRoleDTO;
import com.lzlj.account.role.dto.LzljRoleDTO;
import com.lzlj.account.role.dto.LzljRoleMenuDTO;
import com.lzlj.account.role.dto.LzljRoleQueryDTO;
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

    @SaCheckPermission("lzlj:role:create")
    @Operation(summary = "创建角色")
    @OperationLog(module = "role", operation = "CREATE", content = "创建角色")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody LzljCreateRoleDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @SaCheckPermission("lzlj:role:update")
    @Operation(summary = "更新角色")
    @OperationLog(module = "role", operation = "UPDATE", content = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LzljUpdateRoleDTO dto) {
        roleService.update(id, dto);
        return Result.success();
    }

    @SaCheckPermission("lzlj:role:delete")
    @Operation(summary = "删除角色")
    @OperationLog(module = "role", operation = "DELETE", content = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("lzlj:role:list")
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<LzljRoleDTO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @SaCheckPermission("lzlj:role:list")
    @Operation(summary = "分页查询角色")
    @PostMapping("/page")
    public Result<PageResult<LzljRoleDTO>> page(@RequestBody PageRequest<LzljRoleQueryDTO> pageRequest) {
        return Result.success(roleService.page(pageRequest));
    }

    @SaCheckPermission("lzlj:role:list")
    @Operation(summary = "获取角色已授权菜单的idList")
    @GetMapping("/{id}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenuIdList(id));
    }

    @SaCheckPermission("lzlj:role:list")
    @Operation(summary = "获取角色已授权菜单（树形）")
    @GetMapping("/{id}/menus/tree")
    public Result<List<LzljMenuDTO>> getRoleMenusTree(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenusTree(id));
    }

    @SaCheckPermission("lzlj:role:grant")
    @Operation(summary = "分配菜单权限")
    @OperationLog(module = "role", operation = "GRANT", content = "分配菜单权限")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody LzljRoleMenuDTO dto) {
        roleService.assignMenus(id, dto);
        return Result.success();
    }
}

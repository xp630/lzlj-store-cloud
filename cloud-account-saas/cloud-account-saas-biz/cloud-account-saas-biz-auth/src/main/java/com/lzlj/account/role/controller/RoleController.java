package com.lzlj.account.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.role.dto.CreateRoleDTO;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.role.dto.RoleMenuDTO;
import com.lzlj.account.role.dto.UpdateRoleDTO;
import com.lzlj.account.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @SaCheckPermission("saas:role:create")
    @Operation(summary = "创建角色")
    @OperationLog(module = "role", operation = "CREATE", content = "创建角色")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateRoleDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @SaCheckPermission("saas:role:update")
    @Operation(summary = "更新角色")
    @OperationLog(module = "role", operation = "UPDATE", content = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleDTO dto) {
        roleService.update(id, dto);
        return Result.success();
    }

    @SaCheckPermission("saas:role:delete")
    @Operation(summary = "删除角色")
    @OperationLog(module = "role", operation = "DELETE", content = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("saas:role:list")
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<RoleDTO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @SaCheckPermission("saas:role:list")
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<RoleDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) String loginName,
            @RequestParam(required = false) Integer status) {
        return Result.success(roleService.page(loginName, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @SaCheckPermission("saas:role:list")
    @Operation(summary = "获取角色已授权菜单")
    @GetMapping("/{id}/menus")
    public Result<List<MenuDTO>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenus(id));
    }

    @SaCheckPermission("saas:role:list")
    @Operation(summary = "获取角色已授权菜单（树形）")
    @GetMapping("/{id}/menus/tree")
    public Result<List<MenuDTO>> getRoleMenusTree(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenusTree(id));
    }

    @SaCheckPermission("saas:role:grant")
    @Operation(summary = "分配菜单权限（全量替换：传入的菜单ID列表将替换角色当前所有菜单权限）")
    @OperationLog(module = "role", operation = "GRANT", content = "分配菜单权限")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody RoleMenuDTO dto) {
        roleService.assignMenus(id, dto);
        return Result.success();
    }
}

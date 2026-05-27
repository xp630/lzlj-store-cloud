package com.lzlj.account.role.controller;

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

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateRoleDTO dto) {
        return Result.success(roleService.create(dto));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleDTO dto) {
        roleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<RoleDTO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<RoleDTO>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(roleService.page(keyword, status, pageNum, pageSize));
    }

    @Operation(summary = "获取角色已授权菜单")
    @GetMapping("/{id}/menus")
    public Result<List<MenuDTO>> getRoleMenus(@PathVariable Long id) {
        return Result.success(roleService.getRoleMenus(id));
    }

    @Operation(summary = "分配菜单权限")
    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody RoleMenuDTO dto) {
        roleService.assignMenus(id, dto);
        return Result.success();
    }
}

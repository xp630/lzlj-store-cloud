package com.lzlj.account.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.menu.dto.LzljCreateMenuDTO;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.menu.dto.LzljUpdateMenuDTO;
import com.lzlj.account.menu.service.LzljMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * LZLJ 菜单管理控制器
 * <p>
 * 权限说明：
 * - lzlj:menu:create - 创建菜单
 * - lzlj:menu:update - 更新菜单
 * - lzlj:menu:delete - 删除菜单
 * - lzlj:menu:list - 查看菜单列表/详情/树形
 */
@Tag(name = "LZLJ菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class LzljMenuController {

    private final LzljMenuService menuService;

    @SaCheckPermission("lzlj:menu:create")
    @Operation(summary = "创建菜单", description = "需要权限: lzlj:menu:create")
    @OperationLog(module = "menu", operation = "CREATE", content = "创建菜单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody LzljCreateMenuDTO dto) {
        return Result.success(menuService.create(dto));
    }

    @SaCheckPermission("lzlj:menu:update")
    @Operation(summary = "更新菜单", description = "需要权限: lzlj:menu:update")
    @OperationLog(module = "menu", operation = "UPDATE", content = "更新菜单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LzljUpdateMenuDTO dto) {
        menuService.update(id, dto);
        return Result.success();
    }

    @SaCheckPermission("lzlj:menu:delete")
    @Operation(summary = "删除菜单", description = "需要权限: lzlj:menu:delete")
    @OperationLog(module = "menu", operation = "DELETE", content = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("lzlj:menu:list")
    @Operation(summary = "获取菜单详情", description = "需要权限: lzlj:menu:list")
    @GetMapping("/{id}")
    public Result<LzljMenuDTO> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @SaCheckPermission("lzlj:menu:list")
    @Operation(summary = "获取菜单树", description = "需要权限: lzlj:menu:list")
    @GetMapping("/tree")
    public Result<List<LzljMenuDTO>> getTree() {
        return Result.success(menuService.getTree());
    }

    @SaCheckPermission("lzlj:menu:list")
    @Operation(summary = "获取菜单列表（平铺）", description = "需要权限: lzlj:menu:list")
    @GetMapping("/list")
    public Result<List<LzljMenuDTO>> getList() {
        return Result.success(menuService.getList());
    }

    @SaCheckPermission("lzlj:menu:list")
    @Operation(summary = "获取父菜单下拉列表", description = "需要权限: lzlj:menu:list")
    @GetMapping("/parent")
    public Result<List<LzljMenuDTO>> getParentMenuList() {
        return Result.success(menuService.getParentMenuList());
    }

    @Operation(summary = "获取当前用户可访问的菜单")
    @GetMapping("/my")
    public Result<List<LzljMenuDTO>> getMyMenus() {
        return Result.success(menuService.getMyMenus());
    }

    @SaCheckPermission("lzlj:menu:list")
    @Operation(summary = "获取全部菜单（带授权状态）", description = "需要权限: lzlj:menu:list")
    @GetMapping("/all")
    public Result<List<LzljMenuDTO>> getAllMenusWithChecked(
            @RequestParam(required = false) Long roleId) {
        return Result.success(menuService.getAllMenusWithChecked(roleId));
    }
}

package com.lzlj.account.biz.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.enums.ModuleEnum;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.menu.dto.CreateMenuDTO;
import com.lzlj.account.biz.menu.dto.MenuDTO;
import com.lzlj.account.biz.menu.dto.UpdateMenuDTO;
import com.lzlj.account.biz.menu.service.SaasMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜单管理控制器
 * <p>
 * 权限说明：
 * - saas:menu:create - 创建菜单
 * - saas:menu:update - 更新菜单
 * - saas:menu:delete - 删除菜单
 * - saas:menu:list - 查看菜单列表/详情/树形
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final SaasMenuService menuService;

    @SaCheckPermission("saas:menu:create")
    @Operation(summary = "创建菜单", description = "需要权限: saas:menu:create")
    @OperationLog(module = ModuleEnum.MENU, operation = "CREATE", content = "创建菜单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateMenuDTO dto) {
        return Result.success(menuService.create(dto));
    }

    @SaCheckPermission("saas:menu:update")
    @Operation(summary = "更新菜单", description = "需要权限: saas:menu:update")
    @OperationLog(module = ModuleEnum.MENU, operation = "UPDATE", content = "更新菜单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateMenuDTO dto) {
        menuService.update(id, dto);
        return Result.success();
    }

    @SaCheckPermission("saas:menu:delete")
    @Operation(summary = "删除菜单", description = "需要权限: saas:menu:delete")
    @OperationLog(module = ModuleEnum.MENU, operation = "DELETE", content = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("saas:menu:list")
    @Operation(summary = "获取菜单详情", description = "需要权限: saas:menu:list")
    @GetMapping("/{id}")
    public Result<MenuDTO> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @SaCheckPermission("saas:menu:list")
    @Operation(summary = "获取菜单树", description = "需要权限: saas:menu:list")
    @GetMapping("/tree")
    public Result<List<MenuDTO>> getTree() {
        return Result.success(menuService.getTree());
    }

    @SaCheckPermission("saas:menu:list")
    @Operation(summary = "获取菜单列表（平铺）", description = "需要权限: saas:menu:list")
    @GetMapping("/list")
    public Result<List<MenuDTO>> getList() {
        return Result.success(menuService.getList());
    }

    @SaCheckPermission("saas:menu:list")
    @Operation(summary = "获取父菜单下拉列表", description = "需要权限: saas:menu:list")
    @GetMapping("/parent")
    public Result<List<MenuDTO>> getParentMenuList() {
        return Result.success(menuService.getParentMenuList());
    }

    @Operation(summary = "获取当前用户可访问的菜单")
    @GetMapping("/my")
    public Result<List<MenuDTO>> getMyMenus() {
        return Result.success(menuService.getMyMenus());
    }

    @SaCheckPermission("saas:menu:list")
    @Operation(summary = "获取全部菜单（带授权状态）", description = "需要权限: saas:menu:list")
    @GetMapping("/all")
    public Result<List<MenuDTO>> getAllMenusWithChecked(
            @RequestParam(required = false) Long roleId) {
        return Result.success(menuService.getAllMenusWithChecked(roleId));
    }
}

package com.lzlj.account.menu.controller;

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
 */
@Tag(name = "LZLJ菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class LzljMenuController {

    private final LzljMenuService menuService;

    @Operation(summary = "创建菜单")
    @OperationLog(module = "menu", operation = "CREATE", content = "创建菜单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody LzljCreateMenuDTO dto) {
        return Result.success(menuService.create(dto));
    }

    @Operation(summary = "更新菜单")
    @OperationLog(module = "menu", operation = "UPDATE", content = "更新菜单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LzljUpdateMenuDTO dto) {
        menuService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @OperationLog(module = "menu", operation = "DELETE", content = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public Result<LzljMenuDTO> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<LzljMenuDTO>> getTree() {
        return Result.success(menuService.getTree());
    }

    @Operation(summary = "获取菜单列表（平铺）")
    @GetMapping("/list")
    public Result<List<LzljMenuDTO>> getList() {
        return Result.success(menuService.getList());
    }

    @Operation(summary = "获取父菜单下拉列表")
    @GetMapping("/parent")
    public Result<List<LzljMenuDTO>> getParentMenuList() {
        return Result.success(menuService.getParentMenuList());
    }
}

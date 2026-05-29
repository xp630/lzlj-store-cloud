package com.lzlj.account.user.controller;

import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.user.dto.LzljOrgDTO;
import com.lzlj.account.user.entity.LzljOrg;
import com.lzlj.account.user.service.LzljOrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LZLJ 机构控制器
 */
@Tag(name = "机构管理")
@RestController
@RequestMapping("/org")
@RequiredArgsConstructor
public class LzljOrgController {

    private final LzljOrgService orgService;

    @Operation(summary = "获取机构树")
    @GetMapping("/tree")
    public Result<List<LzljOrgDTO>> getTree() {
        return Result.success(orgService.getTree());
    }

    @Operation(summary = "获取机构详情")
    @GetMapping("/{id}")
    public Result<LzljOrgDTO> getById(@PathVariable Long id) {
        return Result.success(orgService.getById(id));
    }

    @Operation(summary = "获取子机构列表")
    @GetMapping("/{id}/children")
    public Result<List<LzljOrgDTO>> getChildren(@PathVariable Long id) {
        return Result.success(orgService.getChildren(id));
    }

    @Operation(summary = "创建机构")
    @PostMapping
    public Result<Long> create(@RequestBody LzljOrg org) {
        return Result.success(orgService.create(org));
    }

    @Operation(summary = "更新机构")
    @PutMapping
    public Result<Void> update(@RequestBody LzljOrg org) {
        orgService.update(org);
        return Result.success();
    }

    @Operation(summary = "删除机构")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orgService.delete(id);
        return Result.success();
    }
}

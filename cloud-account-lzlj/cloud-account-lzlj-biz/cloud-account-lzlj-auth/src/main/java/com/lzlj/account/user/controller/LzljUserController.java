package com.lzlj.account.user.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.user.dto.LzljUserDTO;
import com.lzlj.account.user.dto.LzljUserLoginDTO;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.service.LzljUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * LZLJ 用户控制器
 */
@Tag(name = "LZLJ用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class LzljUserController {

    private final LzljUserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LzljUserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(java.util.Collections.singletonMap("token", token));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<LzljUserDTO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<LzljUserDTO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<LzljUserDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.page(orgId, keyword, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody LzljUser user) {
        return Result.success(userService.create(user));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody LzljUser user) {
        user.setId(id);
        userService.update(user);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success();
    }

    @Operation(summary = "修改状态")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long userId,
            @RequestParam Integer status) {
        userService.changeStatus(userId, status);
        return Result.success();
    }
}

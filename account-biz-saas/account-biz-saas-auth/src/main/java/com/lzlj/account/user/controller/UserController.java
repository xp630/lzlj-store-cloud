
package com.lzlj.account.user.controller;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.user.dto.UserDTO;
import com.lzlj.account.user.dto.UserLoginDTO;
import com.lzlj.account.user.dto.UserRoleDTO;
import com.lzlj.account.user.entity.User;
import com.lzlj.account.user.service.UserRoleService;
import com.lzlj.account.user.service.UserService;
import com.lzlj.account.user.service.impl.UserCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final UserCacheService userCacheService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(java.util.Collections.singletonMap("token", token));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<UserDTO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.page(orgId, keyword, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody User user) {
        return Result.success(userService.create(user));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user) {
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

    @Operation(summary = "绑定微信")
    @PostMapping("/bind/wx")
    public Result<Void> bindWx(
            @RequestParam Long userId,
            @RequestParam(required = false) String wxOpenid,
            @RequestParam(required = false) String wxMaOpenid) {
        userService.bindWx(userId, wxOpenid, wxMaOpenid);
        return Result.success();
    }

    @Operation(summary = "获取用户角色")
    @GetMapping("/{id}/roles")
    public Result<List<RoleDTO>> getUserRoles(@PathVariable Long id) {
        return Result.success(userRoleService.getUserRoles(id));
    }

    @Operation(summary = "分配用户角色")
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody UserRoleDTO dto) {
        userRoleService.assignRoles(id, dto);
        return Result.success();
    }

    // ==================== 旁路缓存测试接口 ====================

    @Operation(summary = "【测试】旁路缓存 - 获取用户（先查缓存后查DB）")
    @GetMapping("/cache/{id}")
    public Result<UserDTO> getByIdWithCache(@PathVariable Long id) {
        return Result.success(userCacheService.getById(id));
    }

    @Operation(summary = "【测试】旁路缓存 - 穿透防护（空值也缓存）")
    @GetMapping("/cache/protect/{id}")
    public Result<UserDTO> getByIdWithProtection(@PathVariable Long id) {
        return Result.success(userCacheService.getByIdWith穿透Protection(id));
    }
}

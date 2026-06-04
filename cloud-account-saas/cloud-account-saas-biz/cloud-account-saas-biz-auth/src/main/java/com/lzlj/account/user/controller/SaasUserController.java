
package com.lzlj.account.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.user.dto.UserDTO;
import com.lzlj.account.user.dto.UserLoginDTO;
import com.lzlj.account.user.dto.UserRoleDTO;
import com.lzlj.account.user.entity.SaasUser;
import com.lzlj.account.user.service.SaasUserRoleService;
import com.lzlj.account.user.service.SaasUserService;
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
public class SaasUserController {

    private final SaasUserService userService;
    private final SaasUserRoleService userRoleService;
    private final UserCacheService userCacheService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("token", token);
        data.put("loginType", loginDTO.getLoginType() == null ? 1 : loginDTO.getLoginType());
        return Result.success(data);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @SaCheckPermission("saas:user:list")
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<UserDTO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @SaCheckPermission("saas:user:list")
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserDTO>> page(
            PageRequest pageRequest,
            @RequestParam(required = false) Long orgId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.page(orgId, keyword, status, pageRequest.getPageNum(), pageRequest.getPageSize()));
    }

    @SaCheckPermission("saas:user:create")
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SaasUser user) {
        return Result.success(userService.create(user));
    }

    @SaCheckPermission("saas:user:update")
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SaasUser user) {
        user.setId(id);
        userService.update(user);
        return Result.success();
    }

    @SaCheckPermission("saas:user:delete")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("saas:user:password")
    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @SaCheckPermission("saas:user:password")
    @Operation(summary = "重置密码")
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success();
    }

    @SaCheckPermission("saas:user:update")
    @Operation(summary = "修改状态")
    @PostMapping("/status")
    public Result<Void> changeStatus(
            @RequestParam Long userId,
            @RequestParam Integer status) {
        userService.changeStatus(userId, status);
        return Result.success();
    }

    @SaCheckPermission("saas:user:bind")
    @Operation(summary = "绑定微信")
    @PostMapping("/bind/wx")
    public Result<Void> bindWx(
            @RequestParam Long userId,
            @RequestParam(required = false) String wxOpenid,
            @RequestParam(required = false) String wxMaOpenid) {
        userService.bindWx(userId, wxOpenid, wxMaOpenid);
        return Result.success();
    }

    @SaCheckPermission("saas:user:roles")
    @Operation(summary = "获取用户角色")
    @GetMapping("/{id}/roles")
    public Result<List<RoleDTO>> getUserRoles(@PathVariable Long id) {
        return Result.success(userRoleService.getUserRoles(id));
    }

    @SaCheckPermission("saas:user:roles")
    @Operation(summary = "分配用户角色（全量替换：传入的角色ID列表将替换用户当前所有角色）")
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

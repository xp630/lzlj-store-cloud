package com.lzlj.account.permission.service;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SaaS Sa-Token 权限接口实现
 * <p>
 * 实现 StpInterface 接口，供 Sa-Token 框架自动获取用户的权限和角色列表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaasStpInterface implements StpInterface {

    private final PermissionService permissionService;

    /**
     * 获取用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());
        List<String> permissions = new java.util.ArrayList<>(permissionService.getUserPermissions(userId));
        log.debug("Sa-Token 获取用户 {} 权限列表: {}", userId, permissions);
        return permissions;
    }

    /**
     * 获取用户角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // SaaS 暂未实现角色级别校验，返回空列表
        return null;
    }
}

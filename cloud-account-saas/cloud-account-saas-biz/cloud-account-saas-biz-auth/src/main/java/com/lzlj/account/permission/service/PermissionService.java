package com.lzlj.account.permission.service;

import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.role.service.RoleService;
import com.lzlj.account.user.service.SaasUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务
 * <p>
 * 负责加载用户的菜单权限集合
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final SaasUserRoleService userRoleService;
    private final RoleService roleService;

    /**
     * 获取用户的所有菜单权限
     * <p>
     * 权限加载链路：
     * User → UserRole → Role → RoleMenu → Menu
     * <p>
     * 提取 Menu.permission 字段作为权限标识
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    public Set<String> getUserPermissions(Long userId) {
        // 获取用户角色列表
        List<RoleDTO> roles = userRoleService.getUserRoles(userId);
        if (roles == null || roles.isEmpty()) {
            log.debug("用户 {} 无角色，返回空权限集", userId);
            return new HashSet<>();
        }

        Set<String> permissions = new HashSet<>();

        // 遍历每个角色，获取其菜单权限
        for (RoleDTO role : roles) {
            List<MenuDTO> menus = roleService.getRoleMenus(role.getId());
            if (menus == null || menus.isEmpty()) {
                continue;
            }

            // 提取 permission 字段
            for (MenuDTO menu : menus) {
                if (StringUtils.hasText(menu.getPermission())) {
                    permissions.add(menu.getPermission());
                }
            }
        }

        log.debug("用户 {} 权限集: {}", userId, permissions);
        return permissions;
    }

    /**
     * 获取用户菜单列表（用于前端渲染）
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    public List<MenuDTO> getUserMenus(Long userId) {
        List<RoleDTO> roles = userRoleService.getUserRoles(userId);
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        // 合并所有角色的菜单（去重）
        Set<Long> menuIds = new HashSet<>();
        for (RoleDTO role : roles) {
            List<MenuDTO> menus = roleService.getRoleMenus(role.getId());
            if (menus != null) {
                menus.forEach(m -> menuIds.add(m.getId()));
            }
        }

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 返回第一个有菜单的角色的完整菜单树
        for (RoleDTO role : roles) {
            List<MenuDTO> menus = roleService.getRoleMenusTree(role.getId());
            if (menus != null && !menus.isEmpty()) {
                return menus;
            }
        }

        return new ArrayList<>();
    }
}

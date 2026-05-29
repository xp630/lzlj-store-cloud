package com.lzlj.account.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dto.LzljCreateMenuDTO;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.menu.dto.LzljUpdateMenuDTO;
import com.lzlj.account.menu.entity.LzljMenu;
import com.lzlj.account.menu.mapper.LzljMenuDao;
import com.lzlj.account.menu.service.LzljMenuService;
import com.lzlj.account.role.entity.LzljRole;
import com.lzlj.account.role.entity.LzljRoleMenu;
import com.lzlj.account.role.mapper.LzljRoleDao;
import com.lzlj.account.role.mapper.LzljRoleMenuDao;
import com.lzlj.account.user.entity.LzljUserRole;
import com.lzlj.account.user.mapper.LzljUserRoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LZLJ 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljMenuServiceImpl implements LzljMenuService {

    private final LzljMenuDao menuDao;
    private final LzljUserRoleDao userRoleDao;
    private final LzljRoleMenuDao roleMenuDao;
    private final LzljRoleDao roleDao;

    @Override
    public Long create(LzljCreateMenuDTO dto) {
        LzljMenu menu = new LzljMenu();
        BeanUtils.copyProperties(dto, menu);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menuDao.insert(menu);
        log.info("创建菜单成功: id={}, name={}", menu.getId(), menu.getName());
        return menu.getId();
    }

    @Override
    public void update(Long id, LzljUpdateMenuDTO dto) {
        LzljMenu existMenu = menuDao.selectById(id);
        if (existMenu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否将自己设置为父菜单
        if (id.equals(dto.getParentId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "不能将自己设置为父菜单");
        }

        BeanUtils.copyProperties(dto, existMenu);
        menuDao.updateById(existMenu);
        log.info("更新菜单成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        LzljMenu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<LzljMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMenu::getParentId, id);
        if (menuDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "存在子菜单，无法删除");
        }

        menuDao.deleteById(id);
        log.info("删除菜单成功: id={}", id);
    }

    @Override
    public LzljMenuDTO getById(Long id) {
        LzljMenu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(menu);
    }

    @Override
    public List<LzljMenuDTO> getTree() {
        List<LzljMenu> allMenus = getAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<LzljMenuDTO> getList() {
        List<LzljMenu> menus = getAllMenus();
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LzljMenuDTO> getParentMenuList() {
        LambdaQueryWrapper<LzljMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(LzljMenu::getType, 0, 1) // 目录和菜单
               .eq(LzljMenu::getStatus, 1)
               .orderByAsc(LzljMenu::getSort);
        List<LzljMenu> menus = menuDao.selectList(wrapper);
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<LzljMenuDTO> getMyMenus() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new AuthException(ResultCode.UNAUTHORIZED);
        }

        // 获取用户的角色IDs
        LambdaQueryWrapper<LzljUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(LzljUserRole::getUserId, userId);
        List<LzljUserRole> userRoles = userRoleDao.selectList(userRoleWrapper);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(LzljUserRole::getRoleId)
                .collect(Collectors.toSet());

        // 检查是否有 SUPER_ADMIN 角色
        List<LzljRole> roles = roleDao.selectBatchIds(roleIds);
        boolean isSuperAdmin = roles.stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getRoleCode()));

        if (isSuperAdmin) {
            // 超管返回所有菜单
            LambdaQueryWrapper<LzljMenu> allMenuWrapper = new LambdaQueryWrapper<>();
            allMenuWrapper.eq(LzljMenu::getStatus, 1)
                         .orderByAsc(LzljMenu::getSort);
            List<LzljMenu> menus = menuDao.selectList(allMenuWrapper);
            return buildTree(menus, 0L);
        }

        // 获取这些角色的菜单IDs
        LambdaQueryWrapper<LzljRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(LzljRoleMenu::getRoleId, roleIds);
        List<LzljRoleMenu> roleMenus = roleMenuDao.selectList(roleMenuWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(LzljRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 查询菜单并构建树
        LambdaQueryWrapper<LzljMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(LzljMenu::getId, menuIds)
                  .eq(LzljMenu::getStatus, 1)
                  .orderByAsc(LzljMenu::getSort);
        List<LzljMenu> menus = menuDao.selectList(menuWrapper);

        return buildTree(menus, 0L);
    }

    @Override
    public List<LzljMenuDTO> getAllMenusWithChecked(Long roleId) {
        Set<Long> checkedMenuIds = new HashSet<>();

        if (roleId != null && roleId > 0) {
            // 获取角色已授权的菜单IDs
            LambdaQueryWrapper<LzljRoleMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LzljRoleMenu::getRoleId, roleId);
            List<LzljRoleMenu> roleMenus = roleMenuDao.selectList(wrapper);
            checkedMenuIds = roleMenus.stream()
                    .map(LzljRoleMenu::getMenuId)
                    .collect(Collectors.toSet());
        }

        // 获取全部菜单
        List<LzljMenu> allMenus = getAllMenus();

        // 构建树并标注checked状态
        return buildMenuTreeWithChecked(allMenus, 0L, checkedMenuIds);
    }

    private List<LzljMenu> getAllMenus() {
        LambdaQueryWrapper<LzljMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMenu::getStatus, 1)
               .orderByAsc(LzljMenu::getSort);
        return menuDao.selectList(wrapper);
    }

    private List<LzljMenuDTO> buildTree(List<LzljMenu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    LzljMenuDTO dto = convertToDTO(menu);
                    dto.setChildren(buildTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<LzljMenuDTO> buildMenuTreeWithChecked(List<LzljMenu> menus, Long parentId, Set<Long> checkedMenuIds) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    LzljMenuDTO dto = convertToDTO(menu);
                    dto.setChecked(checkedMenuIds.contains(menu.getId()));
                    dto.setChildren(buildMenuTreeWithChecked(menus, menu.getId(), checkedMenuIds));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private LzljMenuDTO convertToDTO(LzljMenu menu) {
        LzljMenuDTO dto = new LzljMenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

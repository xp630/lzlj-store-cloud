package com.lzlj.account.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dao.MenuDao;
import com.lzlj.account.menu.dto.CreateMenuDTO;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.menu.dto.UpdateMenuDTO;
import com.lzlj.account.menu.entity.Menu;
import com.lzlj.account.menu.service.MenuService;
import com.lzlj.account.role.dao.RoleDao;
import com.lzlj.account.role.dao.RoleMenuDao;
import com.lzlj.account.role.entity.Role;
import com.lzlj.account.role.entity.RoleMenu;
import com.lzlj.account.user.dao.UserRoleDao;
import com.lzlj.account.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;
    private final UserRoleDao userRoleDao;
    private final RoleMenuDao roleMenuDao;
    private final RoleDao roleDao;

    @Override
    public Long create(CreateMenuDTO dto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menuDao.insert(menu);
        log.info("创建菜单成功: id={}, name={}", menu.getId(), menu.getName());
        return menu.getId();
    }

    @Override
    public void update(Long id, UpdateMenuDTO dto) {
        Menu existMenu = menuDao.selectById(id);
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
        Menu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, id);
        if (menuDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "存在子菜单，无法删除");
        }

        menuDao.deleteById(id);
        log.info("删除菜单成功: id={}", id);
    }

    @Override
    public MenuDTO getById(Long id) {
        Menu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(menu);
    }

    @Override
    public List<MenuDTO> getTree() {
        List<Menu> allMenus = getAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<MenuDTO> getList() {
        List<Menu> menus = getAllMenus();
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getParentMenuList() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Menu::getType, 0, 1) // 目录和菜单
               .eq(Menu::getStatus, 1)
               .orderByAsc(Menu::getSort);
        List<Menu> menus = menuDao.selectList(wrapper);
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMyMenus() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new AuthException(ResultCode.UNAUTHORIZED);
        }

        // 获取用户的角色IDs
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleDao.selectList(userRoleWrapper);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 检查是否有 SUPER_ADMIN 角色
        List<Role> roles = roleDao.selectBatchIds(roleIds);
        boolean isSuperAdmin = roles.stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getRoleCode()));

        if (isSuperAdmin) {
            // 超管返回所有菜单
            LambdaQueryWrapper<Menu> allMenuWrapper = new LambdaQueryWrapper<>();
            allMenuWrapper.eq(Menu::getStatus, 1)
                         .orderByAsc(Menu::getSort);
            List<Menu> menus = menuDao.selectList(allMenuWrapper);
            return buildTree(menus, 0L);
        }

        // 获取这些角色的菜单IDs
        LambdaQueryWrapper<RoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(RoleMenu::getRoleId, roleIds);
        List<RoleMenu> roleMenus = roleMenuDao.selectList(roleMenuWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 查询菜单并构建树
        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(Menu::getId, menuIds)
                  .eq(Menu::getStatus, 1)
                  .orderByAsc(Menu::getSort);
        List<Menu> menus = menuDao.selectList(menuWrapper);

        return buildTree(menus, 0L);
    }

    @Override
    public List<MenuDTO> getAllMenusWithChecked(Long roleId) {
        Set<Long> checkedMenuIds = new HashSet<>();

        if (roleId != null && roleId > 0) {
            // 获取角色已授权的菜单IDs
            LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoleMenu::getRoleId, roleId);
            List<RoleMenu> roleMenus = roleMenuDao.selectList(wrapper);
            checkedMenuIds = roleMenus.stream()
                    .map(RoleMenu::getMenuId)
                    .collect(Collectors.toSet());
        }

        // 获取全部菜单
        List<Menu> allMenus = getAllMenus();

        // 构建树并标注checked状态
        return buildMenuTreeWithChecked(allMenus, 0L, checkedMenuIds);
    }

    private List<Menu> getAllMenus() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getStatus, 1)
               .orderByAsc(Menu::getSort);
        return menuDao.selectList(wrapper);
    }

    private List<MenuDTO> buildTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuDTO dto = convertToDTO(menu);
                    dto.setChildren(buildTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<MenuDTO> buildMenuTreeWithChecked(List<Menu> menus, Long parentId, Set<Long> checkedMenuIds) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuDTO dto = convertToDTO(menu);
                    dto.setChecked(checkedMenuIds.contains(menu.getId()));
                    dto.setChildren(buildMenuTreeWithChecked(menus, menu.getId(), checkedMenuIds));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

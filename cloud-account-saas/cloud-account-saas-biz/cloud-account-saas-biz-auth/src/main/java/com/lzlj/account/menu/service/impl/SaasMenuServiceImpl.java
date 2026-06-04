package com.lzlj.account.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dao.SaasMenuDao;
import com.lzlj.account.menu.dto.CreateMenuDTO;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.menu.dto.UpdateMenuDTO;
import com.lzlj.account.menu.entity.SaasMenu;
import com.lzlj.account.menu.service.SaasMenuService;
import com.lzlj.account.role.dao.SaasRoleDao;
import com.lzlj.account.role.dao.SaasRoleMenuDao;
import com.lzlj.account.role.entity.SaasRole;
import com.lzlj.account.role.entity.SaasRoleMenu;
import com.lzlj.account.user.dao.SaasUserRoleDao;
import com.lzlj.account.user.entity.SaasUserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasMenuServiceImpl implements SaasMenuService {

    private final SaasMenuDao menuDao;
    private final SaasUserRoleDao userRoleDao;
    private final SaasRoleMenuDao roleMenuDao;
    private final SaasRoleDao roleDao;

    @Override
    public Long create(CreateMenuDTO dto) {
        SaasMenu menu = new SaasMenu();
        BeanUtils.copyProperties(dto, menu);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menuDao.insert(menu);
        log.info("创建菜单成功: id={}, name={}", menu.getId(), menu.getName());
        return menu.getId();
    }

    @Override
    public void update(Long id, UpdateMenuDTO dto) {
        SaasMenu existMenu = menuDao.selectById(id);
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
        SaasMenu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<SaasMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasMenu::getParentId, id);
        if (menuDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "存在子菜单，无法删除");
        }

        menuDao.deleteById(id);
        log.info("删除菜单成功: id={}", id);
    }

    @Override
    public MenuDTO getById(Long id) {
        SaasMenu menu = menuDao.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(menu);
    }

    @Override
    public List<MenuDTO> getTree() {
        List<SaasMenu> allMenus = getAllMenus();
        return buildTree(allMenus, 0L);
    }

    @Override
    public List<MenuDTO> getList() {
        List<SaasMenu> menus = getAllMenus();
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getParentMenuList() {
        LambdaQueryWrapper<SaasMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SaasMenu::getType, 0, 1) // 目录和菜单
               .eq(SaasMenu::getStatus, 1)
               .orderByAsc(SaasMenu::getSort);
        List<SaasMenu> menus = menuDao.selectList(wrapper);
        return menus.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MenuDTO> getMyMenus() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new AuthException(ResultCode.UNAUTHORIZED);
        }

        // 获取用户的角色IDs
        LambdaQueryWrapper<SaasUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SaasUserRole::getUserId, userId);
        List<SaasUserRole> userRoles = userRoleDao.selectList(userRoleWrapper);

        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SaasUserRole::getRoleId)
                .collect(Collectors.toSet());

        // 检查是否有 SUPER_ADMIN 角色
        List<SaasRole> roles = roleDao.selectBatchIds(roleIds);
        boolean isSuperAdmin = roles.stream()
                .anyMatch(r -> "SUPER_ADMIN".equals(r.getRoleCode()));

        if (isSuperAdmin) {
            // 超管返回所有菜单
            LambdaQueryWrapper<SaasMenu> allMenuWrapper = new LambdaQueryWrapper<>();
            allMenuWrapper.eq(SaasMenu::getStatus, 1)
                         .orderByAsc(SaasMenu::getSort);
            List<SaasMenu> menus = menuDao.selectList(allMenuWrapper);
            if (menus.isEmpty()) {
                return Collections.emptyList();
            }
            return buildTree(menus, menus.get(0).getParentId());
        }

        // 获取这些角色的菜单IDs
        LambdaQueryWrapper<SaasRoleMenu> roleMenuWrapper = new LambdaQueryWrapper<>();
        roleMenuWrapper.in(SaasRoleMenu::getRoleId, roleIds);
        List<SaasRoleMenu> roleMenus = roleMenuDao.selectList(roleMenuWrapper);

        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(SaasRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 查询菜单并构建树
        LambdaQueryWrapper<SaasMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(SaasMenu::getId, menuIds)
                  .eq(SaasMenu::getStatus, 1)
                  .orderByAsc(SaasMenu::getSort);
        List<SaasMenu> menus = menuDao.selectList(menuWrapper);
        if (menus.isEmpty()) {
            return Collections.emptyList();
        }

        return buildTree(menus, menus.get(0).getParentId());
    }

    @Override
    public List<MenuDTO> getAllMenusWithChecked(Long roleId) {
        Set<Long> checkedMenuIds = new HashSet<>();

        if (roleId != null && roleId > 0) {
            // 获取角色已授权的菜单IDs
            LambdaQueryWrapper<SaasRoleMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SaasRoleMenu::getRoleId, roleId);
            List<SaasRoleMenu> roleMenus = roleMenuDao.selectList(wrapper);
            checkedMenuIds = roleMenus.stream()
                    .map(SaasRoleMenu::getMenuId)
                    .collect(Collectors.toSet());
        }

        // 获取全部菜单
        List<SaasMenu> allMenus = getAllMenus();

        // 确定树的根节点：用第一个菜单的parentId
        Long rootParentId = 0L;
        if (!allMenus.isEmpty()) {
            SaasMenu firstMenu = allMenus.get(0);
            if (firstMenu.getParentId() != null) {
                rootParentId = firstMenu.getParentId();
            }
        }

        // 构建树并标注checked状态
        return buildMenuTreeWithChecked(allMenus, rootParentId, checkedMenuIds);
    }

    private List<SaasMenu> getAllMenus() {
        LambdaQueryWrapper<SaasMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasMenu::getStatus, 1)
               .orderByAsc(SaasMenu::getSort);
        return menuDao.selectList(wrapper);
    }

    private List<MenuDTO> buildTree(List<SaasMenu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    MenuDTO dto = convertToDTO(menu);
                    dto.setChildren(buildTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<MenuDTO> buildMenuTreeWithChecked(List<SaasMenu> menus, Long parentId, Set<Long> checkedMenuIds) {
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

    private MenuDTO convertToDTO(SaasMenu menu) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

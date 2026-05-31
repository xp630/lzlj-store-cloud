package com.lzlj.account.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dao.MenuDao;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.menu.entity.Menu;
import com.lzlj.account.role.dao.RoleDao;
import com.lzlj.account.role.dao.RoleMenuDao;
import com.lzlj.account.role.dto.CreateRoleDTO;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.role.dto.RoleMenuDTO;
import com.lzlj.account.role.dto.UpdateRoleDTO;
import com.lzlj.account.role.entity.Role;
import com.lzlj.account.role.entity.RoleMenu;
import com.lzlj.account.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final RoleMenuDao roleMenuDao;
    private final MenuDao menuDao;

    @Override
    public List<MenuDTO> getRoleMenusTree(Long roleId) {
        Role role = roleDao.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuDao.selectList(wrapper);

        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toSet());

        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(Menu::getId, menuIds)
                  .eq(Menu::getStatus, 1)
                  .orderByAsc(Menu::getSort);
        List<Menu> menus = menuDao.selectList(menuWrapper);

        return buildMenuTree(menus, 0L);
    }

    private List<MenuDTO> buildMenuTree(List<Menu> menus, Long parentId) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuDTO dto = convertMenuToDTO(menu);
                    dto.setChildren(buildMenuTree(menus, menu.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long create(CreateRoleDTO dto) {
        // 检查编码唯一性
        if (checkCodeExists(dto.getRoleCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        roleDao.insert(role);
        log.info("创建角色成功: id={}, code={}", role.getId(), role.getRoleCode());
        return role.getId();
    }

    @Override
    public void update(Long id, UpdateRoleDTO dto) {
        Role existRole = roleDao.selectById(id);
        if (existRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existRole);
        roleDao.updateById(existRole);
        log.info("更新角色成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        Role role = roleDao.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除角色菜单关联（硬删，避免 @TableLogic + 唯一键 冲突）
        roleMenuDao.deleteByRoleIdHard(id);

        // 删除角色
        roleDao.deleteById(id);
        log.info("删除角色成功: id={}", id);
    }

    @Override
    public RoleDTO getById(Long id) {
        Role role = roleDao.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(role);
    }

    @Override
    public PageResult<RoleDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Role::getRoleName, keyword)
               .eq(status != null, Role::getStatus, status)
               .orderByDesc(Role::getCreateTime);

        IPage<Role> resultPage = roleDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<MenuDTO> getRoleMenus(Long roleId) {
        Role role = roleDao.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 获取角色菜单关联
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuDao.selectList(wrapper);

        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取菜单列表
        List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(Menu::getId, menuIds);
        List<Menu> menus = menuDao.selectList(menuWrapper);

        return menus.stream().map(this::convertMenuToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, RoleMenuDTO dto) {
        Role role = roleDao.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除原有菜单关联（硬删，避免 @TableLogic + 唯一键 冲突）
        roleMenuDao.deleteByRoleIdHard(roleId);

        // 新增菜单关联
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            List<RoleMenu> roleMenus = dto.getMenuIds().stream().map(menuId -> {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());

            // 批量插入
            for (RoleMenu roleMenu : roleMenus) {
                roleMenuDao.insert(roleMenu);
            }
        }

        log.info("分配菜单权限成功: roleId={}, menuIds={}", roleId, dto.getMenuIds());
    }

    private boolean checkCodeExists(String roleCode, Long excludeId) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(Role::getId, excludeId);
        }
        return roleDao.selectCount(wrapper) > 0;
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }

    private MenuDTO convertMenuToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

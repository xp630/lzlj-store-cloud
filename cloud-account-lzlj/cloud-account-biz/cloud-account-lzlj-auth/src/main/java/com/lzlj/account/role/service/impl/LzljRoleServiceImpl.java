package com.lzlj.account.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.menu.entity.LzljMenu;
import com.lzlj.account.menu.mapper.LzljMenuDao;
import com.lzlj.account.role.dto.*;
import com.lzlj.account.role.entity.LzljRole;
import com.lzlj.account.role.entity.LzljRoleMenu;
import com.lzlj.account.role.mapper.LzljRoleDao;
import com.lzlj.account.role.mapper.LzljRoleMenuDao;
import com.lzlj.account.role.service.LzljRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljRoleServiceImpl implements LzljRoleService {

    private final LzljRoleDao roleDao;
    private final LzljRoleMenuDao roleMenuDao;
    private final LzljMenuDao menuDao;

    @Override
    public Long create(LzljCreateRoleDTO dto) {
        if (checkCodeExists(dto.getRoleCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "角色编码已存在");
        }

        LzljRole role = new LzljRole();
        BeanUtils.copyProperties(dto, role);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        roleDao.insert(role);
        log.info("创建角色成功: id={}, code={}", role.getId(), role.getRoleCode());
        return role.getId();
    }

    @Override
    public void update(Long id, LzljUpdateRoleDTO dto) {
        LzljRole existRole = roleDao.selectById(id);
        if (existRole == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existRole);
        roleDao.updateById(existRole);
        log.info("更新角色成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        LzljRole role = roleDao.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除角色菜单关联
        LambdaQueryWrapper<LzljRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljRoleMenu::getRoleId, id);
        roleMenuDao.delete(wrapper);

        // 删除角色
        roleDao.deleteById(id);
        log.info("删除角色成功: id={}", id);
    }

    @Override
    public LzljRoleDTO getById(Long id) {
        LzljRole role = roleDao.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(role);
    }

    @Override
    public PageResult<LzljRoleDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<LzljRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LzljRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), LzljRole::getRoleName, keyword)
               .eq(status != null, LzljRole::getStatus, status)
               .orderByDesc(LzljRole::getCreateTime);

        IPage<LzljRole> resultPage = roleDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<LzljMenuDTO> getRoleMenus(Long roleId) {
        LzljRole role = roleDao.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<LzljRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljRoleMenu::getRoleId, roleId);
        List<LzljRoleMenu> roleMenus = roleMenuDao.selectList(wrapper);

        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> menuIds = roleMenus.stream().map(LzljRoleMenu::getMenuId).collect(Collectors.toList());
        LambdaQueryWrapper<LzljMenu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.in(LzljMenu::getId, menuIds);
        List<LzljMenu> menus = menuDao.selectList(menuWrapper);

        return menus.stream().map(this::convertMenuToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, LzljRoleMenuDTO dto) {
        LzljRole role = roleDao.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除原有菜单关联
        LambdaQueryWrapper<LzljRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljRoleMenu::getRoleId, roleId);
        roleMenuDao.delete(wrapper);

        // 新增菜单关联
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            List<LzljRoleMenu> roleMenus = dto.getMenuIds().stream().map(menuId -> {
                LzljRoleMenu roleMenu = new LzljRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());

            for (LzljRoleMenu roleMenu : roleMenus) {
                roleMenuDao.insert(roleMenu);
            }
        }

        log.info("分配菜单权限成功: roleId={}, menuIds={}", roleId, dto.getMenuIds());
    }

    private boolean checkCodeExists(String roleCode, Long excludeId) {
        LambdaQueryWrapper<LzljRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljRole::getRoleCode, roleCode);
        if (excludeId != null) {
            wrapper.ne(LzljRole::getId, excludeId);
        }
        return roleDao.selectCount(wrapper) > 0;
    }

    private LzljRoleDTO convertToDTO(LzljRole role) {
        LzljRoleDTO dto = new LzljRoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }

    private LzljMenuDTO convertMenuToDTO(LzljMenu menu) {
        LzljMenuDTO dto = new LzljMenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

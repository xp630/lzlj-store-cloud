package com.lzlj.account.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dto.LzljCreateMenuDTO;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.menu.dto.LzljUpdateMenuDTO;
import com.lzlj.account.menu.entity.LzljMenu;
import com.lzlj.account.menu.mapper.LzljMenuDao;
import com.lzlj.account.menu.service.LzljMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljMenuServiceImpl implements LzljMenuService {

    private final LzljMenuDao menuDao;

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

    private LzljMenuDTO convertToDTO(LzljMenu menu) {
        LzljMenuDTO dto = new LzljMenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

package com.lzlj.account.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.menu.dao.MenuDao;
import com.lzlj.account.menu.dto.CreateMenuDTO;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.menu.dto.UpdateMenuDTO;
import com.lzlj.account.menu.entity.Menu;
import com.lzlj.account.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuDao menuDao;

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

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(menu, dto);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}

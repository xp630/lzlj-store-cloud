package com.lzlj.account.menu.service;

import com.lzlj.account.menu.dto.CreateMenuDTO;
import com.lzlj.account.menu.dto.MenuDTO;
import com.lzlj.account.menu.dto.UpdateMenuDTO;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 创建菜单
     */
    Long create(CreateMenuDTO dto);

    /**
     * 更新菜单
     */
    void update(Long id, UpdateMenuDTO dto);

    /**
     * 删除菜单
     */
    void delete(Long id);

    /**
     * 获取菜单详情
     */
    MenuDTO getById(Long id);

    /**
     * 获取菜单树
     */
    List<MenuDTO> getTree();

    /**
     * 获取菜单列表（平铺）
     */
    List<MenuDTO> getList();

    /**
     * 获取父菜单下拉列表
     */
    List<MenuDTO> getParentMenuList();

    /**
     * 获取当前用户可访问的菜单
     */
    List<MenuDTO> getMyMenus();

    /**
     * 获取全部菜单（带授权状态）
     * @param roleId 角色ID，null或0表示未选择角色，所有菜单checked=false
     */
    List<MenuDTO> getAllMenusWithChecked(Long roleId);
}

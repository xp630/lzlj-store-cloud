package com.lzlj.account.role.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.role.dto.CreateRoleDTO;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.role.dto.RoleMenuDTO;
import com.lzlj.account.role.dto.UpdateRoleDTO;
import com.lzlj.account.menu.dto.MenuDTO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 创建角色
     */
    Long create(CreateRoleDTO dto);

    /**
     * 更新角色
     */
    void update(Long id, UpdateRoleDTO dto);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 获取角色详情
     */
    RoleDTO getById(Long id);

    /**
     * 分页查询角色
     */
    PageResult<RoleDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取角色已授权菜单
     */
    List<MenuDTO> getRoleMenus(Long roleId);

    /**
     * 获取角色已授权菜单（树形）
     */
    List<MenuDTO> getRoleMenusTree(Long roleId);

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, RoleMenuDTO dto);
}

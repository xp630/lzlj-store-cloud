package com.lzlj.account.role.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.role.dto.LzljCreateRoleDTO;
import com.lzlj.account.role.dto.LzljRoleDTO;
import com.lzlj.account.role.dto.LzljRoleMenuDTO;
import com.lzlj.account.role.dto.LzljUpdateRoleDTO;

import java.util.List;

/**
 * LZLJ 角色服务接口
 */
public interface LzljRoleService {

    /**
     * 创建角色
     */
    Long create(LzljCreateRoleDTO dto);

    /**
     * 更新角色
     */
    void update(Long id, LzljUpdateRoleDTO dto);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 获取角色详情
     */
    LzljRoleDTO getById(Long id);

    /**
     * 分页查询角色
     */
    PageResult<LzljRoleDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取角色已授权菜单
     */
    List<LzljMenuDTO> getRoleMenus(Long roleId);

    /**
     * 获取角色已授权菜单（树形）
     */
    List<LzljMenuDTO> getRoleMenusTree(Long roleId);

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, LzljRoleMenuDTO dto);
}

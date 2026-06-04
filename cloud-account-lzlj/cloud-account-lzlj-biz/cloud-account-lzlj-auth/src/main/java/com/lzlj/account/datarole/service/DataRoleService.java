package com.lzlj.account.datarole.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.datarole.dto.*;
import com.lzlj.account.datarole.entity.DataRoleCondition;

import java.util.List;

/**
 * 数据角色服务接口
 */
public interface DataRoleService {

    /**
     * 创建数据角色
     */
    DataRoleDTO create(CreateDataRoleDTO dto);

    /**
     * 更新数据角色
     */
    DataRoleDTO update(Long id, UpdateDataRoleDTO dto);

    /**
     * 删除数据角色
     */
    void delete(Long id);

    /**
     * 获取数据角色详情
     */
    DataRoleDTO getById(Long id);

    /**
     * 分页查询数据角色
     */
    PageResult<DataRoleDTO> page(DataRoleQueryDTO query);

    /**
     * 分配数据角色给用户
     */
    void assign(AssignDataRoleDTO dto);

    /**
     * 获取用户的数据角色列表
     */
    List<DataRoleDTO> getUserDataRoles(Long userId);

    /**
     * 获取用户的数据权限条件
     */
    List<DataRoleCondition> getUserDataRoleConditions(Long userId);

    /**
     * 获取当前用户的数据权限条件
     */
    List<DataRoleCondition> getCurrentUserDataRoleConditions();
}

package com.lzlj.account.biz.user.service;

import com.lzlj.account.biz.role.dto.RoleDTO;
import com.lzlj.account.biz.user.dto.UserRoleDTO;

import java.util.List;

/**
 * 用户角色服务接口
 */
public interface SaasUserRoleService {

    /**
     * 获取用户角色
     */
    List<RoleDTO> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, UserRoleDTO dto);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
}

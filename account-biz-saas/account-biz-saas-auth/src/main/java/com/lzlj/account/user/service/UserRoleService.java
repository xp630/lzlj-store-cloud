package com.lzlj.account.user.service;

import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.user.dto.UserRoleDTO;

import java.util.List;

/**
 * 用户角色服务接口
 */
public interface UserRoleService {

    /**
     * 获取用户角色
     */
    List<RoleDTO> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, UserRoleDTO dto);
}

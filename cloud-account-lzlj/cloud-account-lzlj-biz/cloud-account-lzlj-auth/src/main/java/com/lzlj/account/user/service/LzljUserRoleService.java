package com.lzlj.account.user.service;

import com.lzlj.account.role.dto.LzljRoleDTO;

import java.util.List;

/**
 * LZLJ 用户角色服务接口
 */
public interface LzljUserRoleService {

    /**
     * 获取用户角色列表
     */
    List<LzljRoleDTO> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, java.util.List<Long> roleIds);
}

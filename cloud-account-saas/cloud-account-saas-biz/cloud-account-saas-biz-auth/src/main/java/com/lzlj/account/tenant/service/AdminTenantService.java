package com.lzlj.account.tenant.service;

import com.lzlj.account.tenant.dto.AdminTenantDTO;
import com.lzlj.account.tenant.dto.AssignTenantDTO;

import java.util.List;

/**
 * 管理员租户服务接口
 */
public interface AdminTenantService {

    /**
     * 获取管理员可管理的租户列表
     */
    List<AdminTenantDTO> getAdminTenants(Long adminUserId);

    /**
     * 分配管理员可管理的租户
     */
    void assignTenants(Long adminUserId, AssignTenantDTO dto);
}

package com.lzlj.account.tenant.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.tenant.dto.CreateTenantDTO;
import com.lzlj.account.tenant.dto.TenantDTO;
import com.lzlj.account.tenant.dto.UpdateTenantDTO;

/**
 * 租户服务接口
 */
public interface TenantService {

    /**
     * 创建租户
     */
    Long create(CreateTenantDTO dto);

    /**
     * 更新租户
     */
    void update(Long id, UpdateTenantDTO dto);

    /**
     * 删除租户
     */
    void delete(Long id);

    /**
     * 获取租户详情
     */
    TenantDTO getById(Long id);

    /**
     * 根据编码获取租户
     */
    TenantDTO getByCode(String tenantCode);

    /**
     * 分页查询租户
     */
    PageResult<TenantDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 修改租户状态
     */
    void changeStatus(Long id, Integer status);
}

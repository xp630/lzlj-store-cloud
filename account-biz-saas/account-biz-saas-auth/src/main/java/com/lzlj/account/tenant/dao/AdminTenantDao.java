package com.lzlj.account.tenant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.tenant.entity.AdminTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员租户关联 Mapper
 */
@Mapper
public interface AdminTenantDao extends BaseMapper<AdminTenant> {
}

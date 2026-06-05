package com.lzlj.account.biz.tenant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 Mapper
 */
@Mapper
public interface TenantDao extends BaseMapper<Tenant> {
}

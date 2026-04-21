package com.lzlj.store.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.store.user.entity.Organization;
import org.apache.ibatis.annotations.Mapper;

/**
 * 组织Mapper
 */
@Mapper
public interface OrganizationDao extends BaseMapper<Organization> {
}

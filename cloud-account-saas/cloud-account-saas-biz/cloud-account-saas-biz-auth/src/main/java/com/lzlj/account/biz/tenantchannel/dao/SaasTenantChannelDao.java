package com.lzlj.account.biz.tenantchannel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.tenantchannel.entity.SaasTenantChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户渠道 Mapper
 */
@Mapper
public interface SaasTenantChannelDao extends BaseMapper<SaasTenantChannel> {
}

package com.lzlj.account.biz.log.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.log.entity.SaasOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface SaasOperationLogDao extends BaseMapper<SaasOperationLog> {
}

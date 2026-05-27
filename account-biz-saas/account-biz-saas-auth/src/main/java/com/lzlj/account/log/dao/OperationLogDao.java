package com.lzlj.account.log.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.log.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface OperationLogDao extends BaseMapper<OperationLog> {
}

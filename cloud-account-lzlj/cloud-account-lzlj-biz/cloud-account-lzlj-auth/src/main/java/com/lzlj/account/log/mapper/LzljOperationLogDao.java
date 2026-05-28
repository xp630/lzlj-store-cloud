package com.lzlj.account.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.log.entity.LzljOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * LZLJ 操作日志Mapper
 */
@Mapper
public interface LzljOperationLogDao extends BaseMapper<LzljOperationLog> {
}

package com.lzlj.account.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.log.entity.LzljApiLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * LZLJ API日志Mapper
 */
@Mapper
public interface LzljApiLogDao extends BaseMapper<LzljApiLog> {
}

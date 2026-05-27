package com.lzlj.account.log.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.log.entity.ApiLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * API访问日志 Mapper
 */
@Mapper
public interface ApiLogDao extends BaseMapper<ApiLog> {
}

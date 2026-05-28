package com.lzlj.account.openapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.openapi.entity.ApiKey;
import org.apache.ibatis.annotations.Mapper;

/**
 * API密钥 Mapper
 */
@Mapper
public interface ApiKeyDao extends BaseMapper<ApiKey> {
}

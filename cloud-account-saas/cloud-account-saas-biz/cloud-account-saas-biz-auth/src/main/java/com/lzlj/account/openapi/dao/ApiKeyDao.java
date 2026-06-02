package com.lzlj.account.openapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.lzlj.account.openapi.entity.ApiKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * API密钥 Mapper
 */
@Mapper
public interface ApiKeyDao extends BaseMapper<ApiKey> {

    /**
     * 根据 API Key 查询（绕过租户拦截）
     * 用于 OpenAPI 签名验证时查询认证信息
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM saas_auth_api_key WHERE api_key = #{apiKey} AND status = 1 AND deleted = 0")
    ApiKey selectByApiKeyWithoutTenant(@Param("apiKey") String apiKey);
}

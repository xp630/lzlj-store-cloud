package com.lzlj.account.openapi.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.openapi.dto.ApiKeyDTO;
import com.lzlj.account.openapi.dto.CreateApiKeyDTO;
import com.lzlj.account.openapi.dto.UpdateApiKeyDTO;

import java.util.List;

/**
 * API密钥服务接口
 */
public interface ApiKeyService {

    /**
     * 创建API密钥
     */
    ApiKeyDTO create(CreateApiKeyDTO dto);

    /**
     * 更新API密钥
     */
    void update(Long id, UpdateApiKeyDTO dto);

    /**
     * 删除API密钥
     */
    void delete(Long id);

    /**
     * 获取API密钥详情
     */
    ApiKeyDTO getById(Long id);

    /**
     * 分页查询API密钥
     */
    PageResult<ApiKeyDTO> page(Long tenantId, String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据API Key获取密钥信息（用于认证）
     */
    ApiKeyDTO getByApiKey(String apiKey);

    /**
     * 更新最后使用时间
     */
    void updateLastUsedTime(String apiKey);

    /**
     * 启用/禁用API密钥
     */
    void changeStatus(Long id, Integer status);
}

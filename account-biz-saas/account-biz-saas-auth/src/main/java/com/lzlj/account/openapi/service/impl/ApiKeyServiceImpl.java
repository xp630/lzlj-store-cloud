package com.lzlj.account.openapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.openapi.dao.ApiKeyDao;
import com.lzlj.account.openapi.dto.ApiKeyAuthDTO;
import com.lzlj.account.openapi.dto.ApiKeyDTO;
import com.lzlj.account.openapi.dto.CreateApiKeyDTO;
import com.lzlj.account.openapi.dto.UpdateApiKeyDTO;
import com.lzlj.account.openapi.entity.ApiKey;
import com.lzlj.account.openapi.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * API密钥服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyDao apiKeyDao;
    private final CacheManager cacheManager;

    private static final String CACHE_NAME = "apiKeyAuth";

    @Override
    public ApiKeyDTO create(CreateApiKeyDTO dto) {
        // 生成 API Key 和 Secret
        String apiKey = "ak_" + UUID.randomUUID().toString().replace("-", "");
        String apiSecret = "sk_" + UUID.randomUUID().toString().replace("-", "");

        ApiKey apiKeyEntity = new ApiKey();
        BeanUtils.copyProperties(dto, apiKeyEntity);
        apiKeyEntity.setApiKey(apiKey);
        apiKeyEntity.setApiSecret(encryptSecret(apiSecret));
        apiKeyEntity.setStatus(1); // 默认启用
        apiKeyEntity.setRateLimit(dto.getRateLimit() != null ? dto.getRateLimit() : 100);

        apiKeyDao.insert(apiKeyEntity);
        log.info("创建API密钥成功: id={}, apiKey={}", apiKeyEntity.getId(), apiKey);

        // 返回时解密secret显示给用户（只返回一次）
        ApiKeyDTO result = convertToDTO(apiKeyEntity);
        result.setApiSecret(apiSecret); // 返回原始secret
        result.setSecretSaved(true);
        return result;
    }

    @Override
    public void update(Long id, UpdateApiKeyDTO dto) {
        ApiKey existKey = apiKeyDao.selectById(id);
        if (existKey == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existKey);
        apiKeyDao.updateById(existKey);

        // 清除缓存
        evictCache(existKey.getApiKey());

        log.info("更新API密钥成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        ApiKey apiKey = apiKeyDao.selectById(id);
        if (apiKey == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 清除缓存
        evictCache(apiKey.getApiKey());

        apiKeyDao.deleteById(id);
        log.info("删除API密钥成功: id={}", id);
    }

    @Override
    public ApiKeyDTO getById(Long id) {
        ApiKey apiKey = apiKeyDao.selectById(id);
        if (apiKey == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        ApiKeyDTO dto = convertToDTO(apiKey);
        dto.setApiSecret(null); // 详情不返回secret
        return dto;
    }

    @Override
    public PageResult<ApiKeyDTO> page(Long tenantId, String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<ApiKey> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(tenantId != null, ApiKey::getTenantId, tenantId)
               .like(StringUtils.hasText(keyword), ApiKey::getName, keyword)
               .eq(status != null, ApiKey::getStatus, status)
               .orderByDesc(ApiKey::getCreateTime);

        IPage<ApiKey> resultPage = apiKeyDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(apiKey -> {
                    ApiKeyDTO dto = convertToDTO(apiKey);
                    dto.setApiSecret(null); // 列表不返回secret
                    return dto;
                }).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public ApiKeyDTO getByApiKey(String apiKey) {
        // 直接查数据库，避免 @Cacheable 自我调用失效
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getApiKey, apiKey)
               .eq(ApiKey::getStatus, 1)
               .eq(ApiKey::getDeleted, 0);
        ApiKey existKey = apiKeyDao.selectOne(wrapper);
        if (existKey == null) {
            return null;
        }
        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setId(existKey.getId());
        dto.setApiKey(existKey.getApiKey());
        dto.setTenantId(existKey.getTenantId());
        dto.setStatus(existKey.getStatus());
        return dto;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#apiKey", unless = "#result == null")
    public ApiKeyAuthDTO getAuthInfoByApiKey(String apiKey) {
        log.debug("查询API密钥认证信息（DB）: apiKey={}", apiKey);

        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getApiKey, apiKey)
               .eq(ApiKey::getStatus, 1)
               .eq(ApiKey::getDeleted, 0);
        ApiKey existKey = apiKeyDao.selectOne(wrapper);
        if (existKey == null) {
            return null;
        }

        ApiKeyAuthDTO dto = new ApiKeyAuthDTO();
        dto.setId(existKey.getId());
        dto.setApiKey(existKey.getApiKey());
        dto.setApiSecret(existKey.getApiSecret());
        dto.setTenantId(existKey.getTenantId());
        dto.setStatus(existKey.getStatus());

        return dto;
    }

    @Override
    public void updateLastUsedTime(String apiKey) {
        LambdaQueryWrapper<ApiKey> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiKey::getApiKey, apiKey);
        ApiKey updateKey = new ApiKey();
        updateKey.setLastUsedTime(LocalDateTime.now());
        apiKeyDao.update(updateKey, wrapper);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        ApiKey apiKey = apiKeyDao.selectById(id);
        if (apiKey == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        apiKey.setStatus(status);
        apiKeyDao.updateById(apiKey);

        // 清除缓存
        evictCache(apiKey.getApiKey());

        log.info("修改API密钥状态: id={}, status={}", id, status);
    }

    private void evictCache(String apiKey) {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache != null) {
                cache.evict(apiKey);
                log.debug("清除缓存成功: apiKey={}", apiKey);
            }
        } catch (Exception e) {
            log.warn("清除缓存失败: apiKey={}, error={}", apiKey, e.getMessage());
        }
    }

    private ApiKeyDTO convertToDTO(ApiKey apiKey) {
        ApiKeyDTO dto = new ApiKeyDTO();
        BeanUtils.copyProperties(apiKey, dto);
        dto.setSecretSaved(false);
        return dto;
    }

    private String encryptSecret(String secret) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }
}

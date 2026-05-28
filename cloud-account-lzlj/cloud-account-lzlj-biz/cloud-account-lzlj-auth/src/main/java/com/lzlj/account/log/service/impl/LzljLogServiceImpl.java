package com.lzlj.account.log.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.log.entity.LzljApiLog;
import com.lzlj.account.log.entity.LzljOperationLog;
import com.lzlj.account.log.mapper.LzljApiLogDao;
import com.lzlj.account.log.mapper.LzljOperationLogDao;
import com.lzlj.account.log.service.LzljLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * LZLJ 日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljLogServiceImpl implements LzljLogService {

    private final LzljOperationLogDao operationLogDao;
    private final LzljApiLogDao apiLogDao;

    @Override
    @Async
    public void logOperation(Long userId, Long orgId, String username, String module,
                           String operation, String content, Long bizId, String ip, String userAgent) {
        try {
            LzljOperationLog operationLog = new LzljOperationLog();
            operationLog.setUserId(userId);
            operationLog.setOrgId(orgId);
            operationLog.setUsername(username);
            operationLog.setModule(module);
            operationLog.setOperation(operation);
            operationLog.setContent(content);
            operationLog.setBizId(bizId != null ? String.valueOf(bizId) : null);
            operationLog.setIp(ip);
            operationLog.setUserAgent(userAgent);
            operationLog.setCreateTime(LocalDateTime.now());

            operationLogDao.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: userId={}, module={}, operation={}", userId, module, operation, e);
        }
    }

    @Override
    public void logApiAccess(Long apiKeyId, String apiKey, Long orgId, String method,
                           String path, String requestBody, String responseBody,
                           Integer statusCode, Long duration, String ip, String userAgent, String errorMsg) {
        try {
            LzljApiLog apiLog = new LzljApiLog();
            apiLog.setApiKeyId(apiKeyId);
            apiLog.setApiKey(apiKey);
            apiLog.setOrgId(orgId);
            apiLog.setMethod(method);
            apiLog.setPath(path);
            apiLog.setRequestBody(requestBody);
            apiLog.setResponseBody(responseBody);
            apiLog.setStatusCode(statusCode);
            apiLog.setDuration(duration != null ? duration.longValue() : 0L);
            apiLog.setIp(ip);
            apiLog.setUserAgent(userAgent);
            apiLog.setErrorMsg(errorMsg);
            apiLog.setCreateTime(LocalDateTime.now());

            apiLogDao.insert(apiLog);
        } catch (Exception e) {
            log.error("记录API访问日志失败: apiKey={}, path={}", apiKey, path, e);
        }
    }

    @Override
    public IPage<LzljOperationLog> pageOperationLog(Page<LzljOperationLog> page, LambdaQueryWrapper<LzljOperationLog> wrapper) {
        return operationLogDao.selectPage(page, wrapper);
    }

    @Override
    public IPage<LzljApiLog> pageApiLog(Page<LzljApiLog> page, LambdaQueryWrapper<LzljApiLog> wrapper) {
        return apiLogDao.selectPage(page, wrapper);
    }

    @Override
    public LzljOperationLog getOperationLogById(Long id) {
        return operationLogDao.selectById(id);
    }

    @Override
    public LzljApiLog getApiLogById(Long id) {
        return apiLogDao.selectById(id);
    }
}

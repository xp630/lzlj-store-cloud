package com.lzlj.account.log.service.impl;

import com.lzlj.account.log.dao.ApiLogDao;
import com.lzlj.account.log.dao.OperationLogDao;
import com.lzlj.account.log.entity.ApiLog;
import com.lzlj.account.log.entity.OperationLog;
import com.lzlj.account.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final OperationLogDao operationLogDao;
    private final ApiLogDao apiLogDao;

    @Override
    @Async
    public void logOperation(Long userId, Long tenantId, String username, String module,
                             String operation, String content, Long bizId, String ip, String userAgent) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setTenantId(tenantId);
            operationLog.setUsername(username);
            operationLog.setModule(module);
            operationLog.setOperation(operation);
            operationLog.setContent(content);
            operationLog.setBizId(bizId);
            operationLog.setIp(ip);
            operationLog.setUserAgent(userAgent);
            operationLog.setCreateTime(LocalDateTime.now());

            operationLogDao.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: userId={}, module={}, operation={}", userId, module, operation, e);
        }
    }

    @Override
    @Async
    public void logApiAccess(Long apiKeyId, String apiKey, Long tenantId, String method,
                             String path, String requestBody, String responseBody,
                             Integer statusCode, Long duration, String ip, String userAgent, String errorMsg) {
        try {
            ApiLog apiLog = new ApiLog();
            apiLog.setApiKeyId(apiKeyId);
            apiLog.setApiKey(apiKey);
            apiLog.setTenantId(tenantId);
            apiLog.setMethod(method);
            apiLog.setPath(path);
            apiLog.setRequestBody(requestBody);
            apiLog.setResponseBody(responseBody);
            apiLog.setStatusCode(statusCode);
            apiLog.setDuration(duration.intValue());
            apiLog.setIp(ip);
            apiLog.setUserAgent(userAgent);
            apiLog.setErrorMsg(errorMsg);
            apiLog.setCreateTime(LocalDateTime.now());

            apiLogDao.insert(apiLog);
        } catch (Exception e) {
            log.error("记录API访问日志失败: apiKey={}, path={}", apiKey, path, e);
        }
    }
}

package com.lzlj.account.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.log.dao.ApiLogDao;
import com.lzlj.account.log.dao.SaasOperationLogDao;
import com.lzlj.account.log.dto.OperationLogDTO;
import com.lzlj.account.log.dto.OperationLogQueryDTO;
import com.lzlj.account.log.entity.ApiLog;
import com.lzlj.account.log.entity.SaasOperationLog;
import com.lzlj.account.log.service.SaasLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasLogServiceImpl implements SaasLogService {

    private final SaasOperationLogDao operationLogDao;
    private final ApiLogDao apiLogDao;

    @Override
    @Async
    public void logOperation(Long userId, Long tenantId, String username, String module,
                             String operation, String content, Long bizId, String ip, String userAgent, String roles) {
        try {
            SaasOperationLog operationLog = new SaasOperationLog();
            operationLog.setUserId(userId);
            operationLog.setTenantId(tenantId);
            operationLog.setUsername(username);
            operationLog.setModule(module);
            operationLog.setOperation(operation);
            operationLog.setContent(content);
            operationLog.setBizId(bizId);
            operationLog.setIp(ip);
            operationLog.setUserAgent(userAgent);
            operationLog.setRoles(roles);
            operationLog.setCreateTime(LocalDateTime.now());

            operationLogDao.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: userId={}, module={}, operation={}", userId, module, operation, e);
        }
    }

    @Override
    public PageResult<OperationLogDTO> pageOperationLog(PageRequest<OperationLogQueryDTO> pageRequest) {
        OperationLogQueryDTO query = pageRequest.getCondition();
        Page<SaasOperationLog> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<SaasOperationLog> wrapper = new LambdaQueryWrapper<>();
        String username = query.getUsername();
        String module = query.getModule();
        String operation = query.getOperation();
        String startTime = query.getStartTime();
        String endTime = query.getEndTime();
        wrapper.like(StringUtils.hasText(username), SaasOperationLog::getUsername, username)
               .like(StringUtils.hasText(module), SaasOperationLog::getModule, module)
               .eq(StringUtils.hasText(operation), SaasOperationLog::getOperation, operation)
               .ge(StringUtils.hasText(startTime), SaasOperationLog::getCreateTime, startTime)
               .le(StringUtils.hasText(endTime), SaasOperationLog::getCreateTime, endTime)
               .orderByDesc(SaasOperationLog::getCreateTime);

        IPage<SaasOperationLog> resultPage = operationLogDao.selectPage(page, wrapper);

        List<OperationLogDTO> list = resultPage.getRecords().stream().map(log -> {
            OperationLogDTO dto = new OperationLogDTO();
            dto.setId(log.getId());
            dto.setUserId(log.getUserId());
            dto.setUsername(log.getUsername());
            dto.setModule(log.getModule());
            dto.setOperation(log.getOperation());
            dto.setContent(log.getContent());
            dto.setBizId(log.getBizId());
            dto.setIp(log.getIp());
            dto.setUserAgent(log.getUserAgent());
            dto.setRoles(log.getRoles());
            dto.setCreateTime(log.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(list, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
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

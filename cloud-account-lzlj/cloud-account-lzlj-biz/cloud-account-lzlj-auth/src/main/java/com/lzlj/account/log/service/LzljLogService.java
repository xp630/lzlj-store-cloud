package com.lzlj.account.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.log.dto.LzljApiLogQueryDTO;
import com.lzlj.account.log.dto.LzljOperationLogQueryDTO;
import com.lzlj.account.log.entity.LzljApiLog;
import com.lzlj.account.log.entity.LzljOperationLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * LZLJ 日志服务接口
 */
public interface LzljLogService {

    /**
     * 记录操作日志
     */
    void logOperation(Long userId, Long orgId, String username, String module,
                     String operation, String content, Long bizId, String ip, String userAgent,
                     String orgName, String functionalRoles, String dataRoles);

    /**
     * 记录API访问日志
     */
    void logApiAccess(Long apiKeyId, String apiKey, Long orgId, String method,
                      String path, String requestBody, String responseBody,
                      Integer statusCode, Long duration, String ip, String userAgent, String errorMsg);

    /**
     * 分页查询操作日志
     */
    IPage<LzljOperationLog> pageOperationLog(PageRequest<LzljOperationLogQueryDTO> pageRequest);

    /**
     * 分页查询API访问日志
     */
    IPage<LzljApiLog> pageApiLog(PageRequest<LzljApiLogQueryDTO> pageRequest);

    /**
     * 获取操作日志详情
     */
    LzljOperationLog getOperationLogById(Long id);

    /**
     * 获取API访问日志详情
     */
    LzljApiLog getApiLogById(Long id);
}

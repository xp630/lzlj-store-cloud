package com.lzlj.account.biz.log.service;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.biz.log.dto.OperationLogDTO;
import com.lzlj.account.biz.log.dto.OperationLogQueryDTO;

/**
 * 日志服务接口
 */
public interface SaasLogService {

    /**
     * 记录操作日志
     *
     * @param userId    用户ID
     * @param tenantId  租户ID
     * @param username  用户名
     * @param module    模块
     * @param operation 操作类型
     * @param content   操作内容
     * @param bizId     业务ID
     * @param ip        IP地址
     * @param userAgent 用户UA
     * @param roles     操作人角色
     */
    void logOperation(Long userId, Long tenantId, String username, String module,
                     String operation, String content, Long bizId, String ip, String userAgent, String roles);

    /**
     * 分页查询操作日志
     *
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<OperationLogDTO> pageOperationLog(PageRequest<OperationLogQueryDTO> pageRequest);

    /**
     * 记录API访问日志
     *
     * @param apiKeyId     API密钥ID
     * @param apiKey       API公钥
     * @param tenantId     租户ID
     * @param method       HTTP方法
     * @param path         请求路径
     * @param requestBody  请求体
     * @param responseBody 响应体
     * @param statusCode   HTTP状态码
     * @param duration     耗时（毫秒）
     * @param ip           客户端IP
     * @param userAgent    客户端UA
     * @param errorMsg     错误信息
     */
    void logApiAccess(Long apiKeyId, String apiKey, Long tenantId, String method,
                      String path, String requestBody, String responseBody,
                      Integer statusCode, Long duration, String ip, String userAgent, String errorMsg);
}

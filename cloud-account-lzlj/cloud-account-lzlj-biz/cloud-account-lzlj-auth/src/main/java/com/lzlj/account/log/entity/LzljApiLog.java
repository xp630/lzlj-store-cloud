package com.lzlj.account.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ API访问日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_api_log")
public class LzljApiLog extends BaseEntity {

    /**
     * API Key ID
     */
    private Long apiKeyId;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 响应体
     */
    private String responseBody;

    /**
     * HTTP状态码
     */
    private Integer statusCode;

    /**
     * 请求耗时(ms)
     */
    private Long duration;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 组织ID
     */
    private Long orgId;
}

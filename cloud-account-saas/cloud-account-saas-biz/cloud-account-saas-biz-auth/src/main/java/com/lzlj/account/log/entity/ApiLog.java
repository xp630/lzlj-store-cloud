package com.lzlj.account.log.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API访问日志实体
 */
@Data
@TableName("saas_auth_api_log")
public class ApiLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * API密钥ID
     */
    private Long apiKeyId;

    /**
     * API公钥
     */
    private String apiKey;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * HTTP方法
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
     * 响应耗时（毫秒）
     */
    private Integer duration;

    /**
     * 客户端IP
     */
    private String ip;

    /**
     * 客户端UA
     */
    private String userAgent;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 请求时间
     */
    private LocalDateTime createTime;
}

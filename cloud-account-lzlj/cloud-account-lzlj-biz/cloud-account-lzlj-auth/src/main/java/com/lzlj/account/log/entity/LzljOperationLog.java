package com.lzlj.account.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_operation_log")
public class LzljOperationLog extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 业务ID
     */
    private String bizId;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 组织ID
     */
    private Long orgId;
}

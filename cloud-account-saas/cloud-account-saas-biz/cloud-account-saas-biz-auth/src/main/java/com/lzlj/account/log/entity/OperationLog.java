package com.lzlj.account.log.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("saas_auth_operation_log")
public class OperationLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 模块（如：user, role, menu）
     */
    private String module;

    /**
     * 操作类型（如：CREATE, UPDATE, DELETE）
     */
    private String operation;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 浏览器UA
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private LocalDateTime createTime;
}

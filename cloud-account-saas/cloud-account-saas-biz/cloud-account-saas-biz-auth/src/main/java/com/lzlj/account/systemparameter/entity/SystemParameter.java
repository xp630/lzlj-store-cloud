package com.lzlj.account.systemparameter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 系统参数实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_system_parameter")
public class SystemParameter extends BaseEntity {

    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 参数编码（全局唯一）
     */
    private String paramKey;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 参数类型（STRING/INTEGER/BOOLEAN/DECIMAL）
     */
    private String paramType;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
}

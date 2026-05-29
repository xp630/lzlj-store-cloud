package com.lzlj.account.systemparameter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 系统参数实体（平台级数据）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_system_parameter")
public class LzljSystemParameter extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String paramKey;

    private String paramName;

    private String paramValue;

    private String paramType;

    private Integer status;
}

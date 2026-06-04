package com.lzlj.account.datarole.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据角色条件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_data_role_condition")
public class DataRoleCondition extends BaseEntity {

    /**
     * 数据角色ID
     */
    private Long dataRoleId;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 操作符: =, !=, >, <, >=, <=, IN, LIKE, BETWEEN
     */
    private String operator;

    /**
     * 值类型: 1:固定值 2:动态值
     */
    private Integer valueType;

    /**
     * 固定值
     */
    private String fieldValue;

    /**
     * 动态值key: currentUser.orgId, currentUser.userId
     */
    private String dynamicValueKey;

    /**
     * 逻辑操作符: AND, OR
     */
    private String logicalOperator;

    /**
     * 条件分组编号(用于分组)
     */
    private Integer conditionGroup;

    /**
     * 排序
     */
    private Integer sort;
}

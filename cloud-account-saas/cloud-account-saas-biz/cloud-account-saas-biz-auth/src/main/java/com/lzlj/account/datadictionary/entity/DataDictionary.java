package com.lzlj.account.datadictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_data_dictionary")
public class DataDictionary extends BaseEntity {

    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典编码（全局唯一）
     */
    private String dictCode;

    /**
     * 字典类型（用于分组）
     */
    private String dictType;

    /**
     * 字典标签（显示名称）
     */
    private String dictLabel;

    /**
     * 字典值（存储值）
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
}

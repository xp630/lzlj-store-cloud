package com.lzlj.account.datadictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 数据字典实体（平台级数据）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_data_dictionary")
public class LzljDataDictionary extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictCode;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sort;

    private Integer status;
}

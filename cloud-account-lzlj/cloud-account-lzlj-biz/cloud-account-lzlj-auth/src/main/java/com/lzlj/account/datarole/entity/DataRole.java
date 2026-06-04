package com.lzlj.account.datarole.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_data_role")
public class DataRole extends BaseEntity {

    /**
     * 数据角色名称
     */
    private String dataRoleName;

    /**
     * 数据角色编码
     */
    private String dataRoleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}

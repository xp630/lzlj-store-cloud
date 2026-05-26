package com.lzlj.account.common.core.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户基础实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class TenantEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID（冗余字段，用于快速查询）
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;

    /**
     * 组织ID（经销商/门店所属组织）
     */
    private Long orgId;

    /**
     * 门店ID（如果是门店级别）
     */
    private Long shopId;

    /**
     * 用户ID（如果是用户级别）
     */
    private Long userId;
}

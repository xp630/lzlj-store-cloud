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
}

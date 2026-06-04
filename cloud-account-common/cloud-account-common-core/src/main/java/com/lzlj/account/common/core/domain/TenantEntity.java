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
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;

}

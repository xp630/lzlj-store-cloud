package com.lzlj.account.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 用户角色关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_user_role")
public class LzljUserRole extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 组织ID
     */
    private Long orgId;
}

package com.lzlj.account.biz.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体（平台级数据）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_user_role")
public class SaasUserRole extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;
}

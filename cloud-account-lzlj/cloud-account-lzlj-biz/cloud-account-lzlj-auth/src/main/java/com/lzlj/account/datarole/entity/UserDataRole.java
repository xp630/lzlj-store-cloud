package com.lzlj.account.datarole.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据角色关联实体
 */
@Data
@TableName("lzlj_auth_user_data_role")
@InterceptorIgnore(tenantLine = "true")
public class UserDataRole {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 数据角色ID
     */
    private Long dataRoleId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

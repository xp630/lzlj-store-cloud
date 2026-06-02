package com.lzlj.account.sms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短信验证码实体
 */
@Data
@TableName("saas_auth_sms_code")
public class SmsCode {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;

    /**
     * 类型: login/register/reset_pwd
     */
    private String type;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态: 0未使用 1已使用 2已过期
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 使用时间
     */
    private LocalDateTime usedAt;
}

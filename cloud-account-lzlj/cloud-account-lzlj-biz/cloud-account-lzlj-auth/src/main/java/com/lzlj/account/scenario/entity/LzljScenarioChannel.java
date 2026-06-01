package com.lzlj.account.scenario.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * LZLJ 业务场景与支付通道关联实体
 */
@Data
@TableName("lzlj_auth_scenario_channel")
public class LzljScenarioChannel {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    private Long scenarioId;

    /**
     * 支付通道ID
     */
    private Long channelId;

    /**
     * 通道配置JSON
     */
    private String config;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    private Integer deleted;
}

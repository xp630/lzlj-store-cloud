package com.lzlj.account.scenario.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 业务场景实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_scenario")
public class LzljScenario extends BaseEntity {

    /**
     * 关联母商户ID
     */
    private Long merchantId;

    /**
     * 场景代码，如 B2B/B2C/C2C
     */
    private String scenarioCode;

    /**
     * 场景名称
     */
    private String scenarioName;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}

package com.lzlj.account.datarole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据角色条件DTO
 */
@Data
@Schema(description = "数据角色条件")
public class ConditionDTO {

    @Schema(description = "条件ID(更新时需要)")
    private Long id;

    @Schema(description = "字段名")
    private String fieldName;

    @Schema(description = "操作符: =, !=, >, <, >=, <=, IN, LIKE, BETWEEN")
    private String operator;

    @Schema(description = "值类型: 1:固定值 2:动态值")
    private Integer valueType;

    @Schema(description = "固定值")
    private String fieldValue;

    @Schema(description = "动态值key: currentUser.orgId, currentUser.userId")
    private String dynamicValueKey;

    @Schema(description = "逻辑操作符: AND, OR")
    private String logicalOperator;

    @Schema(description = "条件分组编号")
    private Integer conditionGroup;

    @Schema(description = "排序")
    private Integer sort;
}

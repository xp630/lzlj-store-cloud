package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 参数类型枚举
 */
@Getter
@AllArgsConstructor
public enum ParamTypeEnum {

    STRING("字符串", "STRING"),
    INTEGER("整数", "INTEGER"),
    BOOLEAN("布尔值", "BOOLEAN"),
    DECIMAL("小数", "DECIMAL");

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型编码
     */
    private final String code;
}

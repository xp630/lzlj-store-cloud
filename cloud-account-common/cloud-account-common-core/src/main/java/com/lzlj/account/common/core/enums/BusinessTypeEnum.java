package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 经营类型枚举
 */
@Getter
@AllArgsConstructor
public enum BusinessTypeEnum {

    PERSONAL(1, "个人"),
    ENTERPRISE(2, "企业经营"),
    INDIVIDUAL(3, "个体经营");

    private final Integer code;
    private final String description;

    public static BusinessTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (BusinessTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

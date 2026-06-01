package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商户开户状态枚举
 */
@Getter
@AllArgsConstructor
public enum MerchantAccountStatusEnum {

    NOT_OPENED(0, "未开户"),
    OPENING(1, "开户中"),
    OPENED(2, "已开户"),
    FAILED(3, "开户失败");

    private final Integer code;
    private final String description;

    public static MerchantAccountStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MerchantAccountStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}

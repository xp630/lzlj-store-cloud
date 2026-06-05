package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 支付方式枚举
 */
@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {

    WECHAT("微信支付", "WECHAT"),
    ALIPAY("支付宝", "ALIPAY"),
    BANK_CARD("银行卡", "BANK_CARD"),
    QUICK_PASS("云闪付", "QUICK_PASS"),
    POS("POS机", "POS");

    /**
     * 支付方式名称
     */
    private final String name;

    /**
     * 支付方式编码
     */
    private final String code;

    /**
     * 根据编码获取名称
     */
    public static String getNameByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code))
                .findFirst()
                .map(PaymentMethodEnum::getName)
                .orElse(code);
    }
}

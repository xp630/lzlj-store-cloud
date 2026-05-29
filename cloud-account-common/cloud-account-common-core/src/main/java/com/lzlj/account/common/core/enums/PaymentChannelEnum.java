package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付通道枚举（渠道）
 */
@Getter
@AllArgsConstructor
public enum PaymentChannelEnum {

    UNIONPAY("银联", "UNIONPAY"),
    NETBANK("网商", "NETBANK");

    /**
     * 通道名称
     */
    private final String name;

    /**
     * 通道编码
     */
    private final String code;
}

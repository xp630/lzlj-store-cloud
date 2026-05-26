package com.lzlj.account.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAIL(400, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    // 服务端错误 5xx
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误 1xxx
    PARAM_ERROR(1001, "参数错误"),
    DATA_NOT_FOUND(1002, "数据不存在"),
    DATA_ALREADY_EXISTS(1003, "数据已存在"),
    VERIFY_CODE_ERROR(1004, "验证码错误"),
    VERIFY_CODE_EXPIRED(1005, "验证码已过期"),

    // 认证错误 2xxx
    TOKEN_INVALID(2001, "Token无效"),
    TOKEN_EXPIRED(2002, "Token已过期"),
    TOKEN_REQUIRED(2003, "Token不能为空"),
    PASSWORD_ERROR(2004, "密码错误"),
    ACCOUNT_DISABLED(2005, "账号已被禁用"),
    ACCOUNT_LOCKED(2006, "账号已被锁定"),

    // 权限错误 3xxx
    PERMISSION_DENIED(3001, "权限不足"),
    NO_PERMISSION(3002, "无权限访问此资源"),

    // 业务规则错误 4xxx
    STOCK_NOT_ENOUGH(4001, "库存不足"),
    ORDER_EXPIRED(4002, "订单已过期"),
    ORDER_PAID(4003, "订单已支付"),
    ORDER_CANCELLED(4004, "订单已取消"),
    PAYMENT_FAILED(4005, "支付失败"),
    REFUND_FAILED(4006, "退款失败"),
    FLASH_SALE_NOT_STARTED(4007, "秒杀未开始"),
    FLASH_SALE_ENDED(4008, "秒杀已结束"),
    FLASH_SALE_LIMITED(4009, "秒杀名额已满"),

    // 限流错误 5xxx
    RATE_LIMITED(5001, "请求过于频繁，请稍后重试"),
    SYSTEM_BUSY(5002, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String message;
}

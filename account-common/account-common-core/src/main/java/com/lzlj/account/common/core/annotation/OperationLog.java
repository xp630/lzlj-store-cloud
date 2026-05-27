package com.lzlj.account.common.core.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    String module();

    /**
     * 操作类型：CREATE, UPDATE, DELETE, QUERY, etc.
     */
    String operation();

    /**
     * 操作描述，支持 SpEL 表达式
     * 如："创建用户: #{#dto.username}"
     */
    String content() default "";

    /**
     * 是否记录请求参数
     */
    boolean logParams() default false;

    /**
     * 是否记录响应结果
     */
    boolean logResult() default false;
}

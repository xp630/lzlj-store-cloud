package com.lzlj.account.common.core.aspect;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.tenant.TenantContext;
import com.lzlj.account.common.core.utils.ServletUtils;
import com.lzlj.account.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final LogService logService;

    @Around("@annotation(com.lzlj.account.common.core.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        Object result = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            throw e;
        } finally {
            // 异步记录日志
            try {
                recordLog(annotation, signature, result);
            } catch (Exception e) {
                log.error("记录操作日志失败", e);
            }
        }
    }

    @Async
    protected void recordLog(OperationLog annotation, MethodSignature signature, Object result) {
        try {
            Long userId = UserContext.getUserId();
            Long tenantId = TenantContext.getTenantId();
            String username = UserContext.getUsername();
            String ip = ServletUtils.getClientIp();
            String userAgent = ServletUtils.getUserAgent();

            // 构建操作内容
            String content = annotation.content();
            if (content.isEmpty()) {
                content = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
            }

            logService.logOperation(
                userId,
                tenantId,
                username,
                annotation.module(),
                annotation.operation(),
                content,
                extractBizId(result),
                ip,
                userAgent
            );
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
        }
    }

    private Long extractBizId(Object result) {
        if (result == null) {
            return null;
        }
        // 尝试从结果中提取ID
        try {
            if (result instanceof Long) {
                return (Long) result;
            }
            java.lang.reflect.Field idField = result.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(result);
        } catch (Exception e) {
            return null;
        }
    }
}

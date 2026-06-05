package com.lzlj.account.common.aspect;

import com.lzlj.account.common.core.annotation.OperationLog;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.tenant.TenantContext;
import com.lzlj.account.common.core.utils.ServletUtils;
import com.lzlj.account.biz.log.event.OperationLogEvent;
import com.lzlj.account.biz.role.dto.RoleDTO;
import com.lzlj.account.biz.user.service.SaasUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final SaasUserRoleService userRoleService;

    @Around("@annotation(com.lzlj.account.common.core.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        // 在主线程中捕获上下文（避免 ThreadLocal 在异步方法中丢失）
        Long userId = UserContext.getUserId() != null ? UserContext.getUserId() : 0L;
        Long tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId() : 0L;
        String username = UserContext.getUsername();
        String functionalRoles = UserContext.getFunctionalRoles();
        String dataRoles = UserContext.getDataRoles();
        String roles = (functionalRoles != null ? functionalRoles : "") + (dataRoles != null && !dataRoles.isEmpty() ? "," + dataRoles : "");
        // 如果上下文没有角色信息，从用户角色服务获取
        if ((roles == null || roles.isEmpty()) && userId > 0) {
            try {
                List<RoleDTO> userRoleList = userRoleService.getUserRoles(userId);
                if (userRoleList != null && !userRoleList.isEmpty()) {
                    roles = userRoleList.stream().map(RoleDTO::getRoleCode).collect(Collectors.joining(","));
                }
            } catch (Exception e) {
                log.warn("获取用户角色信息失败: userId={}", userId, e);
            }
        }
        String ip = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();

        Object result = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            throw e;
        } finally {
            // 发布事件异步记录日志
            try {
                String content = annotation.content();
                if (content.isEmpty()) {
                    content = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
                }
                eventPublisher.publishEvent(new OperationLogEvent(
                        userId, tenantId, username, annotation.module().name(),
                        annotation.operation(), content, extractBizId(result), ip, userAgent, roles
                ));
            } catch (Exception e) {
                log.error("发布操作日志事件失败", e);
            }
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

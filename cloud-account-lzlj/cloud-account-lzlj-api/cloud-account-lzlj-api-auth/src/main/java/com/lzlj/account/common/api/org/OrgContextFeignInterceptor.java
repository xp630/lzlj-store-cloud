package com.lzlj.account.common.api.org;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LZLJ Feign 组织上下文传递拦截器
 * 在服务间调用时自动传递 X-Org-Id header
 */
@Slf4j
@Component
public class OrgContextFeignInterceptor implements RequestInterceptor {

    public static final String HEADER_ORG_ID = "X-Org-Id";

    // 当前组织的 Context（需要在 LZLJ 用户登录时设置）
    private static final ThreadLocal<Long> ORG_CONTEXT = new ThreadLocal<>();

    @Override
    public void apply(RequestTemplate template) {
        Long orgId = getOrgId();
        if (orgId != null) {
            template.header(HEADER_ORG_ID, String.valueOf(orgId));
            log.debug("Feign调用传递组织ID: {}", orgId);
        }
    }

    public static void setOrgId(Long orgId) {
        ORG_CONTEXT.set(orgId);
    }

    public static Long getOrgId() {
        return ORG_CONTEXT.get();
    }

    public static void clear() {
        ORG_CONTEXT.remove();
    }
}

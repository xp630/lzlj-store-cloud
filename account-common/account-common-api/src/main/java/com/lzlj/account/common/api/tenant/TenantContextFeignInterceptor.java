package com.lzlj.account.common.api.tenant;

import com.lzlj.account.common.core.tenant.TenantContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign 租户上下文传递拦截器
 * 在服务间调用时自动传递 X-Tenant-Id header
 */
@Slf4j
@Component
public class TenantContextFeignInterceptor implements RequestInterceptor {

    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Override
    public void apply(RequestTemplate template) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            template.header(HEADER_TENANT_ID, String.valueOf(tenantId));
            log.debug("Feign调用传递租户ID: {}", tenantId);
        }
    }
}

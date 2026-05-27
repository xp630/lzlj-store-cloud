package com.lzlj.account.log;

import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.tenant.TenantContext;
import com.lzlj.account.log.entity.ApiLog;
import com.lzlj.account.log.entity.OperationLog;
import com.lzlj.account.log.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 日志功能测试
 */
@SpringBootTest
class LogServiceTest {

    @Autowired
    private LogService logService;

    @Test
    void testLogOperation() {
        // 设置上下文
        UserContext.setUserId(1L);
        UserContext.setUsername("admin");
        TenantContext.setTenantId(0L);

        // 记录操作日志
        logService.logOperation(
            1L, 0L, "admin",
            "menu", "CREATE",
            "创建菜单: 测试菜单",
            100L,
            "127.0.0.1",
            "Mozilla/5.0"
        );

        // 验证上下文被正确使用
        assertEquals(1L, UserContext.getUserId());
        assertEquals("admin", UserContext.getUsername());
        assertEquals(0L, TenantContext.getTenantId());

        // 清理
        UserContext.clear();
        TenantContext.clear();
    }

    @Test
    void testLogApiAccess() {
        // 设置上下文
        UserContext.setUserId(1L);
        TenantContext.setTenantId(1L);

        // 记录API日志
        logService.logApiAccess(
            1L, "ak_test123",
            1L,
            "POST",
            "/api/order",
            "{\"product_id\": 1}",
            "{\"order_id\": 100}",
            200,
            150L,
            "192.168.1.1",
            "Java/11",
            null
        );

        assertEquals(1L, UserContext.getUserId());
        assertEquals(1L, TenantContext.getTenantId());

        // 清理
        UserContext.clear();
        TenantContext.clear();
    }
}

package com.lzlj.account.common.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.lzlj.account.common.core.tenant.TenantContext;
import net.sf.jsqlparser.schema.Column;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 插件拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 租户拦截器 - 自动添加 tenant_id 条件
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor(
                new TenantLineHandler() {
                    @Override
                    public Column getTenantId() {
                        // 返回租户ID列，用于 WHERE 条件构建
                        return new Column("tenant_id");
                    }

                    @Override
                    public String getTenantIdColumn() {
                        return "tenant_id";
                    }

                    @Override
                    public boolean ignoreTable(String tableName) {
                        // 租户表本身不需要租户隔离（租户表是系统表，不是业务数据）
                        if ("saas_auth_tenant".equals(tableName)) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean ignoreInsert(java.util.List<net.sf.jsqlparser.schema.Column> columns, String tenantIdColumn) {
                        // 不忽略INSERT，让MyBatis Plus自动处理租户ID填充
                        return false;
                    }
                }
        );
        interceptor.addInnerInterceptor(tenantInterceptor);

        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
                this.strictInsertFill(metaObject, "version", Integer.class, 0);
                // 自动填充租户ID
                this.strictInsertFill(metaObject, "tenantId", Long.class, TenantContext.getTenantId());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}

# TenantContext 使用指南

## 概述

`TenantContext` 是租户上下文字符串工具类，基于 `ThreadLocal` 实现，用于在请求生命周期内传递租户信息。

## 核心功能

### 1. 租户ID管理

```java
// 设置租户ID
TenantContext.setTenantId(1L);

// 获取租户ID（dev环境无租户时默认返回1）
Long tenantId = TenantContext.getTenantId();
```

### 2. 忽略租户隔离（重点）

**场景**：当需要查询不属于当前租户的数据时（如获取用户角色、获取其他租户的数据），可以使用 `setIgnoreTenant` 临时跳过租户拦截。

```java
// 开启忽略租户隔离
TenantContext.setIgnoreTenant(true);
try {
    // 此时所有 MyBatis 查询都不会添加 tenant_id 条件
    List<UserRole> roles = userRoleDao.selectList(wrapper);
    // ...
} finally {
    // 必须关闭，否则影响其他请求
    TenantContext.setIgnoreTenant(false);
}
```

## MybatisPlusConfig 中的优先级

在 `MybatisPlusConfig` 中，`ignoreTable` 方法的判断顺序：

```java
@Override
public boolean ignoreTable(String tableName) {
    // 1. 最高优先级：TenantContext 强制标志
    if (TenantContext.isIgnoreTenant() != null && TenantContext.isIgnoreTenant()) {
        return true;
    }
    // 2. 次优先级：EntityTableScanner 表级判断
    return !EntityTableScanner.needTenant(tableName);
}
```

## 典型使用场景

### 场景1：获取用户的角色列表

```java
@Override
public List<RoleDTO> getUserRoles(Long userId) {
    // 获取用户角色时，需要忽略租户隔离
    // 因为用户可能属于某个租户，但其角色数据可能需要跨租户查询
    TenantContext.setIgnoreTenant(true);
    try {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleDao.selectList(wrapper);

        List<Long> roleIds = userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
        List<Role> roles = roleDao.selectBatchIds(roleIds);

        return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
    } finally {
        TenantContext.setIgnoreTenant(false);
    }
}
```

### 场景2：SaaS 后台获取所有租户的用户

```java
public List<UserDTO> listAllTenantsUsers() {
    TenantContext.setIgnoreTenant(true);
    try {
        return userDao.selectList(null);  // 不加租户条件
    } finally {
        TenantContext.setIgnoreTenant(false);
    }
}
```

## 注意事项

### 1. 必须配对使用 try-finally

```java
// 正确写法
TenantContext.setIgnoreTenant(true);
try {
    // 业务逻辑
} finally {
    TenantContext.setIgnoreTenant(false);  // 必须关闭
}

// 错误写法 - 可能导致租户隔离失效
TenantContext.setIgnoreTenant(true);
List<User> users = userDao.selectList(null);
TenantContext.setIgnoreTenant(false);  // 如果上面抛异常，永远不会执行
```

### 2. 线程安全

`TenantContext` 基于 `ThreadLocal`，每个线程独立。在异步场景（如 `@Async`、`多线程`）中使用时，需要手动传递租户上下文。

### 3. 与 @InterceptorIgnore 的区别

| 特性 | TenantContext | @InterceptorIgnore |
|------|-------------|-------------------|
| 作用范围 | 方法级，但通过 Context 控制 | 方法级（自定义 SQL） |
| 控制粒度 | 可动态开关 | 编译时确定 |
| BaseMapper 支持 | ✅ 完全支持 | ❌ 不支持 |
| 适用场景 | 临时跳过、跨租户查询 | 固定跳过特定方法 |

## 清除上下文

在请求结束时，`TenantContextInitializerFilter` 会自动清除：

```java
// TenantContextInitializerFilter.java
finally {
    TenantContext.clear();  // 清除所有 ThreadLocal
}
```

手动清除（通常不需要）：

```java
TenantContext.clear();
```

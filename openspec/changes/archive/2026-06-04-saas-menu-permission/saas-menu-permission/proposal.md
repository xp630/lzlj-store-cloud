# Sa-Token 菜单级权限控制

## Why

当前系统已实现菜单级别的功能权限分配（Role → RoleMenu → Menu），但存在以下问题：

1. **权限校验缺失**：Menu.permission 字段定义了权限标识（如 `menu:user`），但后端接口从未校验，任何登录用户都能访问所有接口
2. **维护成本高**：传统方案按接口分配权限（每个接口一个 permission 字符串），导致 N 个接口 = N 个权限字符串，维护成本高
3. **无法精细化控制**：无法做到"用户能看到菜单A，但只能操作菜单A下部分接口"

**解决思路**：菜单 = 功能单元，菜单下所有接口自动继承该菜单的权限标识。用户只需被分配菜单，就能访问该菜单下所有接口。

## What Changes

### 1. 引入 Sa-Token 权限框架

在 SaaS 和 LZLJ 的 auth 服务中引入 Sa-Token：

```
cloud-account-saas-biz-auth/pom.xml
cloud-account-lzlj-biz-auth/pom.xml
    + sa-token-spring-boot3-starter
```

### 2. 登录改造（复用现有架构）

改造登录逻辑，使用 Sa-Token 替代 JWT Token：

```
变更前:
  String token = generateToken(user);  // JWT
  return token;

变更后:
  StpUtil.login(user.getId());  // Sa-Token登录
  Set<String> permissions = loadUserPermissions(user.getId());
  StpUtil.getSession().set("permissions", permissions);
  return StpUtil.getTokenValue();
```

**权限加载复用现有链路**：
```
User → UserRole → Role → RoleMenu → Menu
        ↓
    提取 Menu.permission 集合
```

### 3. Sa-Token 配置

新增配置类：
- `SaasSaTokenConfig.java` - SaaS 服务配置
- `LzljSaTokenConfig.java` - LZLJ 服务配置

配置内容：
- Token 存储：复用现有 RedisHelper
- 拦截路径：`/api/**`
- 权限校验：基于 Menu.permission 字段

### 4. Controller 注解添加

在接口上添加 `@SaCheckPermission` 注解：

```java
@SaCheckPermission("menu:user")
@PostMapping
public Result<Void> create() { }

@SaCheckPermission("menu:user")
@GetMapping
public Result<List<UserDTO>> list() { }

// 需要同时拥有多个菜单权限
@SaCheckPermission({"menu:user", "menu:export"})
@GetMapping("/export")
public Result<String> export() { }
```

### 5. Gateway 改造

改造 JwtAuthFilter，支持 Sa-Token Token 验证：

```
变更前：只支持 JWT Token
变更后：同时支持 JWT Token 和 Sa-Token Token
```

### 6. 权限缓存与失效

权限变更时自动清理缓存：
- 用户角色变更 → 清理该用户 Sa-Token Session
- 角色菜单变更 → 清理该角色下所有用户缓存

## Capabilities

### New Capabilities
- `sa-token-auth`: Sa-Token 认证框架集成
- `menu-permission-check`: 基于菜单的权限校验

### Modified Capabilities
- `saas-auth-login`: 登录改用 Sa-Token
- `lzlj-auth-login`: 登录改用 Sa-Token

## Impact

- **SAAS**:
  - 新增 `SaasSaTokenConfig.java`
  - 修改 `SaasUserServiceImpl.java` (login)
  - 修改 `JwtAuthFilter.java` (支持 Sa-Token)
  - Controller 添加 `@SaCheckPermission` 注解

- **LZLJ**:
  - 新增 `LzljSaTokenConfig.java`
  - 修改 `LzljUserServiceImpl.java` (login)
  - 修改 `JwtAuthFilter.java` (支持 Sa-Token)
  - Controller 添加 `@SaCheckPermission` 注解

- **API 变更**:
  - 登录接口返回 Token 格式不变（Sa-Token Token 兼容）
  - 新增注解 `@SaCheckPermission` 标注在 Controller 方法上

- **缓存**:
  - Sa-Token Session 存储在 Redis（复用现有 RedisHelper）
  - 权限变更时清理 Session

## 工时估算

| 阶段 | 工作项 | 工时(人/天) |
|------|--------|-------------|
| 1 | 依赖添加 + 配置类 | 1 |
| 2 | 登录改造 + 权限加载 | 2 |
| 3 | Gateway 改造 | 1 |
| 4 | Controller 注解 (SaaS) | 1.5 |
| 5 | Controller 注解 (LZLJ) | 1.5 |
| 6 | 测试 + 联调 | 2 |
| **合计** | | **9** |

## 风险

1. **过渡期兼容性**：Gateway 需同时支持 JWT 和 Sa-Token，建议按环境逐步切换
2. **Token 有效期**：Sa-Token 默认 30 分钟，需与现有 JWT 有效期配置一致
3. **权限变更生效**：角色菜单变更后需清理缓存，建议添加清理接口
4. **超级管理员**：建议增加 `role_code = 'ADMIN'` 特殊角色，自动放行所有接口

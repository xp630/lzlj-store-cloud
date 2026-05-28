# 新人上手指南

## 一、环境准备

### 1.1 必需中间件

| 中间件 | 版本 | 用途 | 默认端口 |
|--------|------|------|----------|
| JDK | 17+ | Java 运行时 | - |
| Maven | 3.6+ | 项目构建 | - |
| MySQL | 8.0+ | 主数据库 | 3306 |
| Redis | 6.0+ | 缓存 | 6379 |
| Nacos | 2.3.x | 注册中心 + 配置中心 | 8848 |

### 1.2 开发工具推荐

- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse
- **数据库客户端**: DBeaver / Navicat
- **API 测试**: Postman / Apifox
- **Redis 客户端**: RedisInsight

### 1.3 环境变量配置

```bash
# .bashrc 或 .zshrc
export JAVA_HOME=/path/to/jdk17
export MAVEN_HOME=/path/to/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

---

## 二、代码导入与构建

### 2.1 克隆代码

```bash
git clone https://github.com/xp630/lzlj-store-cloud.git
cd lzlj-store-cloud
```

### 2.2 Maven 构建

```bash
# 全量构建
mvn clean install -DskipTests

# 仅编译
mvn compile

# 指定模块构建
mvn clean install -pl cloud-account-lzlj/cloud-account-lzlj-biz/cloud-account-lzlj-auth -am -DskipTests
```

### 2.3 导入 IDE

1. File → Open → 选择项目根目录
2. 选择 Maven 项目导入
3. 等待依赖下载完成（约 5-10 分钟）

---

## 三、配置文件说明

### 3.1 三种名称的区别

| 名称类型 | 示例 | 用途 | 在哪里配置 |
|----------|------|------|-----------|
| **模块名** (artifactId) | `cloud-account-lzlj-auth` | Maven 项目标识 | pom.xml |
| **Nacos 服务名** | `lzlj-auth` | 服务注册与发现 | application.yml → `spring.application.name` |
| **包名** (package) | `com.lzlj.account.user` | 代码组织 | Java 文件 package 声明 |

**重要**: 模块名 ≠ Nacos 服务名！两者可以不同。

### 3.2 配置文件位置

```
本地配置（优先级低）:
  src/main/resources/application.yml
  src/main/resources/application-dev.yml

Nacos 配置（优先级高）:
  Nacos 控制台 → 配置列表 → lzlj-auth.yml, lzlj-user.yml 等
```

### 3.3 本地配置示例

```yaml
# application.yml (cloud-account-lzlj-auth 示例)
server:
  port: 9092

spring:
  application:
    name: lzlj-auth        # ← 这就是 Nacos 服务名！
  config:
    import: optional:nacos:lzlj-auth.yml   # 导入 Nacos 配置
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      username: nacos
      password: nacos
```

---

## 四、服务启动顺序

### 4.1 启动顺序（必须按序启动）

```
1. Nacos (注册中心 + 配置中心)
   ↓
2. MySQL + Redis (基础设施)
   ↓
3. cloud-account-lzlj-gateway (网关，端口 18080)
   ↓
4. cloud-account-lzlj-auth (用户服务，端口 9092) ← 基础服务，其他服务可能依赖
   ↓
5. cloud-account-lzlj-user (LZLJ 用户服务，端口 9093)
```

### 4.2 启动检查清单

- [ ] Nacos 已启动 (http://localhost:8848/nacos)
- [ ] MySQL 已启动
- [ ] Redis 已启动
- [ ] Nacos 中已导入配置（shared-configs: common.yml 等）
- [ ] 数据库已初始化（执行 SQL 脚本）
  - `sql/saas_tenant.sql` - 租户表
  - `sql/saas_auth_user.sql` - 用户表

---

## 五、API 文档入口

### 5.1 各服务 Swagger 地址

| 服务 | URL | 说明 |
|------|-----|------|
| Gateway | http://localhost:18080/swagger-ui.html | 统一入口 |
| lzlj-auth | http://localhost:9092/swagger-ui.html | 用户服务 |
| lzlj-user | http://localhost:9093/swagger-ui.html | LZLJ 用户服务 |

### 5.2 OpenAPI 文档

| 服务 | OpenAPI JSON URL |
|------|-----------------|
| lzlj-auth | http://localhost:9092/v3/api-docs |

---

## 六、项目结构速览

### 6.1 目录结构

```
lzlj-store-cloud/
├── cloud-account-lzlj/                    # LZLJ 主业务模块
│   ├── cloud-account-lzlj-api/          # API 接口定义
│   │   └── cloud-account-lzlj-api-auth/ # Auth Feign 接口
│   ├── cloud-account-lzlj-biz/           # 业务实现
│   │   ├── cloud-account-lzlj-auth/     # 用户服务 (9092)
│   │   └── cloud-account-lzlj-user/     # LZLJ 用户服务 (9093)
│   └── cloud-account-lzlj-entrance/      # 入口层
│       └── cloud-account-lzlj-gateway/  # API 网关 (18080)
├── cloud-account-saas/                    # SaaS 业务模块
│   └── cloud-account-saas-entrance/
│       └── cloud-account-saas-gateway/   # SaaS 网关
├── cloud-account-common/                  # 公共模块
│   ├── cloud-account-common-core/       # 核心代码 (Result, Exception)
│   ├── cloud-account-common-api/        # Feign 接口定义
│   └── cloud-account-common-redis/      # Redis 配置
└── docs/                                  # 文档
```

### 6.2 包结构（以 cloud-account-lzlj-auth 为例）

```
com.lzlj.account.user/
└── user/
    ├── LzljAuthApplication.java    # 启动类
    ├── controller/                 # Controller
    ├── service/                    # Service
    │   ├── UserService.java       # 接口
    │   └── impl/                  # 实现
    ├── mapper/                    # MyBatis Mapper
    ├── entity/                    # 数据库实体（MyBatis 映射）
    ├── dto/                       # 数据传输对象（入参和出参统一使用）
    ├── config/                    # 配置类
    ├── log/                       # 日志（操作日志、API日志）
    │   ├── aspect/                # 日志切面
    │   ├── entity/                # 日志实体
    │   ├── event/                 # 日志事件
    │   ├── mapper/                # 日志 Mapper
    │   └── service/               # 日志服务
    └── handler/                   # 处理器
```

### 6.3 Common 模块说明

`cloud-account-common` 是公共模块，被所有业务模块依赖，**不包含业务逻辑**。

```
cloud-account-common/
├── cloud-account-common-core/     # 核心共享代码
│   └── com/lzlj/account/common/core/
│       ├── Result.java              # 统一响应封装
│       ├── ResultCode.java          # 响应码枚举
│       ├── GlobalExceptionHandler.java  # 全局异常处理
│       ├── BusinessException.java   # 业务异常
│       ├── AuthException.java       # 认证异常
│       └── tenant/                  # 租户相关
│           ├── TenantContext.java   # 租户上下文
│           └── TenantEntity.java    # 租户实体基类
│
├── cloud-account-common-api/      # 服务间接口契约
│   └── com/lzlj/account/common/api/
│       ├── feign/                  # FeignClient 接口定义
│       │   └── UserFeignClient.java
│       └── dto/                    # 跨服务共享 DTO
│           └── UserDTO.java
│
└── cloud-account-common-redis/    # Redis 配置
    └── RedissonConfig.java
```

**约束**：
- ✅ 所有业务模块都可以依赖
- ❌ 不得依赖任何 `cloud-account-*-biz` 模块
- ❌ 不得包含 Entity、Service 实现

### 6.4 包结构详解（统一 DTO 约定）

```
com.lzlj.account.{模块名}.{模块名}/
│
├── controller/          # 控制层
│   └── 职责：接收请求、参数校验、调用 Service
│   └── 命名：{业务}Controller，如 UserController
│
├── service/             # 服务层
│   ├── {业务}Service.java      # 服务接口（简单场景可省略）
│   └── impl/
│       └── {业务}ServiceImpl.java  # 服务实现
│
├── mapper/              # MyBatis Mapper 接口
│   └── 命名：{业务}Mapper.java
│
├── entity/              # 数据库实体（MyBatis 映射用，本地不跨服务）
│   └── 命名：{业务}Entity.java，如 UserEntity
│
├── dto/                 # 数据传输对象（统一使用，不再区分 VO）
│   └── 命名：{业务}DTO.java，如 UserDTO、UserCreateDTO
│
├── config/              # 配置类
│   └── 命名：{功能}Config.java，如 RedisConfig
│
├── log/                 # 日志相关
│   ├── aspect/          # 日志切面
│   ├── entity/          # 日志实体
│   ├── event/           # 日志事件
│   ├── mapper/          # 日志 Mapper
│   └── service/         # 日志服务
│
└── handler/             # 处理器（如 Sentinel BlockHandler）
    └── 命名：{功能}Handler.java，如 GlobalExceptionHandler
```

**统一 DTO 约定**：

| 类型 | 位置 | 用途 | 说明 |
|------|------|------|------|
| Entity | `{模块}/entity/` | 数据库映射 | MyBatis-Plus 用 `@TableName`，仅本地使用 |
| DTO | `{模块}/dto/` | 统一数据传输 | **不再区分 VO**，所有数据传递都用 DTO |

**为什么不用 VO？**
- 难以预判接口是否会跨服务共享
- DTO 既可用于内部传递，也可跨服务传递
- 减少类型数量，降低复杂度

**Entity 唯一用途**：MyBatis-Plus 数据库表映射，标注 `@TableName("表名")`

### 6.5 各层依赖方向

```
┌─────────────────────────────────────────────────────┐
│                    Controller                        │
│              接收请求，返回 Result<T>                 │
└─────────────────────┬───────────────────────────────┘
                      │ 调用
                      ▼
┌─────────────────────────────────────────────────────┐
│                     Service                          │
│              业务逻辑处理                             │
└─────────────────────┬───────────────────────────────┘
                      │ 调用
                      ▼
┌─────────────────────────────────────────────────────┐
│                      Mapper                          │
│              MyBatis Mapper 操作数据库               │
└─────────────────────────────────────────────────────┘

跨服务调用：
┌─────────────────────────────────────────────────────┐
│        FeignClient (定义在 cloud-account-common-api) │
└─────────────────────┬───────────────────────────────┘
                      │ HTTP 调用
                      ▼
┌─────────────────────────────────────────────────────┐
│              远程服务 Controller                      │
└─────────────────────────────────────────────────────┘
```

---

## 七、常见开发场景

### 7.1 如何新增一个 API

1. **在 Controller 中添加接口**

```java
// UserController.java
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/{id}")
    public Result<UserDTO> getById(@PathVariable Long id) {
        return userService.getById(id);
    }
}
```

2. **在 Service 中实现业务逻辑**

```java
// UserServiceImpl.java (impl)
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public UserDTO getById(Long id) {
        UserEntity entity = userMapper.selectById(id);
        return BeanCopyUtils.copy(entity, UserDTO.class);
    }
}
```

### 7.2 如何调用其他服务

1. **在 cloud-account-common-api 或 cloud-account-lzlj-api 中定义 FeignClient**

```java
// cloud-account-common-api/feign/UserFeignClient.java
@FeignClient(name = "lzlj-auth", path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);
}
```

2. **在调用方注入使用**

```java
// RoleController.java (在 lzlj-auth 中)
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private UserFeignClient userFeignClient;  // 调用用户服务

    @GetMapping("/owner/{roleId}")
    public Result<UserDTO> getRoleOwner(@PathVariable Long roleId) {
        // 调用远程用户服务
        Result<UserDTO> userResult = userFeignClient.getById(roleId);
        return Result.success(userResult.getData());
    }
}
```

### 7.3 如何添加数据库表

1. 创建 Entity 类

```java
// MenuEntity.java
@Data
@TableName("saas_auth_menu")
public class MenuEntity {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private Integer type;
    private Integer status;
}
```

2. 创建 Mapper

```java
// MenuMapper.java
@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {
}
```

3. 执行 SQL 脚本（在 Nacos 或本地）

### 7.4 如何配置服务降级

参考文档: `docs/feign-degradation.md`

```java
// Fallback 类实现
@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public Result<UserDTO> getById(Long id) {
        return Result.fail("服务暂时不可用");
    }
}

// 使用 Fallback
@FeignClient(name = "lzlj-auth", path = "/user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    // ...
}
```

---

## 八、多租户数据隔离

### 8.1 隔离方案

采用 **共享数据库 + tenant_id 字段** 方案实现多租户数据隔离。

```
┌─────────────────────────────────────────────────────┐
│                   共享数据库                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │ tenant_id=1 │  │ tenant_id=2 │  │ tenant_id=3 │  │
│  │   租户A数据  │  │   租户B数据  │  │   租户C数据  │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────┘
```

### 8.2 核心组件

| 组件 | 文件位置 | 用途 |
|------|----------|------|
| TenantContext | `cloud-account-common-core/.../tenant/TenantContext.java` | ThreadLocal 持有当前请求的租户ID |
| TenantContextInitializerFilter | `cloud-account-common-core/.../tenant/TenantContextInitializerFilter.java` | 从 X-Tenant-Id header 提取租户ID |
| TenantContextFeignInterceptor | `cloud-account-common-api/.../tenant/TenantContextFeignInterceptor.java` | Feign 调用时传递租户ID |
| TenantLineInnerInterceptor | MyBatis Plus 租户拦截器 | 自动添加 tenant_id 过滤条件 |
| TenantEntity | `cloud-account-common-core/.../domain/TenantEntity.java` | 租户实体基类 |

### 8.3 租户ID传递流程

```
请求 → Gateway (JWT解析) → X-Tenant-Id Header → TenantContextInitializerFilter → TenantContext
                                                              ↓
                                              MyBatis Plus 拦截器自动过滤
                                                              ↓
                                                      只返回当前租户数据
```

### 8.4 环境鉴权规则

| 环境 | 是否需要 JWT 鉴权 | 说明 |
|------|-----------------|------|
| dev | ❌ 否 | 开发环境跳过鉴权 |
| test | ✅ 是 | 测试环境需要鉴权 |
| prod | ✅ 是 | 生产环境需要鉴权 |

### 8.5 租户表结构

```sql
-- 租户表
CREATE TABLE saas_auth_tenant (
    id BIGINT PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    tenant_code VARCHAR(50) NOT NULL COMMENT '租户编码',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_code (tenant_code)
) COMMENT '租户表';

-- 用户表（继承 TenantEntity）
CREATE TABLE saas_auth_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(20) COMMENT '盐值',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    org_id BIGINT COMMENT '组织ID',
    -- ... 其他字段
) COMMENT '用户表';
```

### 8.6 租户管理 API

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建租户 | POST | /tenant | 创建新租户 |
| 查询租户 | GET | /tenant/{id} | 根据ID查询租户 |
| 分页查询租户 | GET | /tenant/page | 分页查询租户列表 |
| 更新租户 | PUT | /tenant/{id} | 更新租户信息 |
| 删除租户 | DELETE | /tenant/{id} | 删除租户 |

**创建租户示例**：

```bash
curl -X POST http://localhost:9092/tenant \
  -H "Content-Type: application/json" \
  -d '{
    "tenantName": "测试租户",
    "tenantCode": "test001",
    "contactName": "张三",
    "contactPhone": "13800138000"
  }'
```

### 8.7 如何使用租户实体

实体类继承 `TenantEntity` 即可自动获得租户隔离能力：

```java
// 用户实体继承 TenantEntity
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_user")
public class UserEntity extends TenantEntity {
    private String username;
    private String password;
    // ...
}
```

**查询时**：MyBatis Plus 自动添加 `WHERE tenant_id = ?` 条件

**新增时**：MetaObjectHandler 自动填充 `tenant_id` 字段

---

## 九、调试验证

### 9.1 验证服务注册

访问 Nacos 控制台：http://localhost:8848/nacos
→ 服务管理 → 服务列表 → 应看到所有已启动服务

### 9.2 验证网关路由

```bash
# 通过网关访问用户服务
curl http://localhost:18080/user/1

# 直接访问用户服务
curl http://localhost:9092/user/1
```

### 9.3 验证服务间调用

1. 启动 lzlj-auth 和 lzlj-user
2. 调用 lzlj-user 的接口，该接口内部调用 lzlj-auth
3. 查看返回结果

---

## 十、问题排查

### 10.1 服务无法启动

| 症状 | 可能原因 | 解决方案 |
|------|----------|----------|
| 端口占用 | 9092 端口被占用 | `lsof -i:9092` 查找进程 |
| Nacos 连接失败 | Nacos 未启动 | 启动 Nacos |
| 数据库连接失败 | MySQL 未启动或配置错误 | 检查 application.yml |

### 10.2 Feign 调用失败

| 症状 | 可能原因 | 解决方案 |
|------|----------|----------|
| 404 Not Found | 路径不匹配 | 检查 @RequestMapping 和 @GetMapping 路径 |
| 500 Error | 远程服务异常 | 查看远程服务日志 |
| 超时 | 远程服务响应慢 | 增加超时配置 |

### 10.3 Nacos 配置不生效

1. 确认配置已添加到 Nacos
2. 确认 `spring.config.import` 配置正确
3. 确认 namespace 和 group 匹配

---

## 十一、参考资料

| 文档 | 说明 |
|------|------|
| `docs/architecture-convention.md` | 架构规范 |
| `docs/package-convention.md` | 包命名规范 |
| `docs/feign-degradation.md` | Feign 降级方案 |
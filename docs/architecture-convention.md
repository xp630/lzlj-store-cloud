# LZLJ Cloud 架构规范

## 一、模块结构

```
lzlj-cloud/
├── cloud-account-lzlj/          # LZLJ 主业务模块
│   ├── cloud-account-lzlj-api/  # API 接口定义
│   │   └── cloud-account-lzlj-api-auth/  # Auth Feign 接口
│   ├── cloud-account-lzlj-biz/  # 业务实现
│   │   ├── cloud-account-lzlj-auth/  # LZLJ 认证服务 (9294)
│   │   └── cloud-account-lzlj-user/  # LZLJ 用户服务 (9093)
│   └── cloud-account-lzlj-entrance/   # 入口层
│       └── cloud-account-lzlj-gateway/ # LZLJ 网关 (28080)
├── cloud-account-saas/           # SaaS 业务模块
│   └── cloud-account-saas-entrance/
│       └── cloud-account-saas-gateway/ # SaaS 网关
└── cloud-account-common/          # 公共模块（待建设）
    ├── cloud-account-common-core/     # 核心公共代码
    ├── cloud-account-common-database/ # 数据库
    └── cloud-account-common-redis/    # Redis
```

---

## 二、模块职责定义

### 2.1 cloud-account-common（公共模块）

**职责**：被所有业务模块依赖，提供共享基础设施。

```
cloud-account-common/
├── cloud-account-common-core/     # 核心共享代码
│   └── 包含：
│       ├── Result<T>           # 统一响应封装
│       ├── ResultCode          # 响应码枚举
│       ├── GlobalExceptionHandler # 全局异常处理
│       ├── BusinessException    # 业务异常
│       ├── AuthException       # 认证异常
│       ├── TenantEntity         # 租户实体基类
│       └── 其他通用工具类
│
├── cloud-account-common-database/ # 数据库组件
│   └── 包含：
│       └── MybatisPlusConfig  # MyBatis-Plus 配置
│
├── cloud-account-common-redis/    # Redis 组件
│   └── RedissonConfig       # Redisson 客户端配置
│
└── cloud-account-common-api/      # 服务间接口契约
    └── 包含：
        ├── feign/             # FeignClient 定义
        │   └── UserFeignClient  # 用户服务调用接口
        └── dto/               # 跨服务共享 DTO
```

**约束**：
- ✅ 所有业务模块都可以依赖
- ❌ 不得依赖任何 `cloud-account-*-biz` 模块
- ❌ 不得包含业务逻辑（Entity、Service 实现）

---

### 2.2 各 common 模块职责

| 模块 | 职责 | 主要依赖 |
|------|------|---------|
| `cloud-account-common-core` | 核心基础：Result、Exception、工具类、实体基类 | Spring Boot Web、Nacos、Sentinel、JWT |
| `cloud-account-common-database` | 数据库访问 | MyBatis-Plus、Druid、MySQL |
| `cloud-account-common-redis` | Redis 客户端：缓存、分布式锁 | Redisson |
| `cloud-account-common-api` | Feign 接口定义 | OpenFeign |

---

### 2.3 cloud-account-lzlj-biz（业务模块）

**职责**：实现具体业务逻辑，不共享给其他模块。

```
cloud-account-lzlj/                         # LZLJ 主业务父模块
│
├── cloud-account-lzlj-api/                # API 接口定义
│   └── cloud-account-lzlj-api-auth/       # Auth Feign 接口
│
├── cloud-account-lzlj-biz/                # 业务实现
│   │
│   ├── cloud-account-lzlj-auth/           # LZLJ 认证服务
│   │   ├── 包名：com.lzlj.account.user
│   │   ├── 端口：9294
│   │   ├── 职责：
│   │   │   ├── 用户注册/登录
│   │   │   ├── JWT 认证
│   │   │   ├── 用户信息管理
│   │   │   ├── 租户管理
│   │   │   ├── 菜单管理
│   │   │   ├── 角色管理
│   │   │   └── 操作日志
│   │   └── 依赖：common-core、common-database、common-redis、common-api
│   │
│   └── cloud-account-lzlj-user/           # LZLJ 用户服务
│       ├── 包名：com.lzlj.account.user
│       ├── 端口：9093
│       ├── 职责：
│       │   └── 定制化用户功能
│       └── 依赖：common-core、common-database、common-redis
│
└── cloud-account-lzlj-entrance/           # 入口层
    └── cloud-account-lzlj-gateway/        # LZLJ 网关
        ├── 包名：com.lzlj.account.gateway
        ├── 端口：28080
        └── 职责：
            ├── 路由转发（Route）
            ├── 统一鉴权（JWT）
            ├── 限流熔断（Sentinel）
            └── 请求日志
```

**约束**：
- ✅ 可以依赖 `cloud-account-common` 任意子模块
- ✅ 可以依赖其他服务的 `FeignClient`
- ❌ 不得依赖其他 `cloud-account-*-biz` 的实现代码
- ❌ 不得被其他 `cloud-account-*-biz` 直接依赖

---

### 2.4 模块依赖关系图

```
                                ┌─────────────────────┐
                                │ cloud-account-lzlj  │
                                │     -entrance        │
                                │ cloud-account-lzlj  │
                                │     -gateway         │
                                │      (28080)        │
                                └──────────┬──────────┘
                                           │
                      ┌────────────────────┼────────────────────┐
                      │                    │                    │
                      ▼                    ▼                    ▼
            ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
            │cloud-account-   │ │cloud-account-   │ │cloud-account-   │
            │common(api)      │ │common(redis)    │ │common(core)     │
            └────────┬────────┘ └────────┬────────┘ └────────┬────────┘
                     │                   │                   │
           ┌─────────┴─────────┐        │                   │
           │                   │        │                   │
           ▼                   ▼        ▼                   ▼
    ┌─────────────┐    ┌─────────────┐ ┌─────────────────────────────┐
    │cloud-account│    │cloud-account│ │      cloud-account-lzlj     │
    │ -lzlj-api   │    │ -lzlj-biz   │ │                             │
    │  (父模块)    │    │  (父模块)    │ │  ┌──────────────────────┐   │
    └──────┬──────┘    └──────┬──────┘ │  │cloud-account-lzlj-auth│   │
           │                   │        │  │    (9294) 认证服务   │   │
           ▼                   ▼        │  └──────────────────────┘   │
    ┌─────────────┐    ┌─────────────┐ │  ┌──────────────────────┐   │
    │cloud-account│    │cloud-account│ │  │cloud-account-lzlj-user│   │
    │ -lzlj-api- │    │ -lzlj-auth  │ │  │    (9093) 用户服务   │   │
    │  -auth     │    │   (9294)    │ │  └──────────────────────┘   │
    └─────────────┘    └─────────────┘ └─────────────────────────────┘
```

---

### 2.5 各模块一句话说明

| 模块 | 一句话说明 |
|------|-----------|
| `cloud-account-common-core` | 核心基础：Result、Exception、实体基类、工具类 |
| `cloud-account-common-database` | 数据库：MyBatis-Plus、Druid、MySQL |
| `cloud-account-common-redis` | Redis 客户端：Redisson 缓存和分布式锁 |
| `cloud-account-common-api` | 服务间接口契约：FeignClient + DTO |
| `cloud-account-lzlj` | LZLJ 主业务父模块 |
| `cloud-account-lzlj-api` | LZLJ API 接口定义 |
| `cloud-account-lzlj-api-auth` | Auth Feign 接口 |
| `cloud-account-lzlj-biz` | LZLJ 业务实现父模块 |
| `cloud-account-lzlj-auth` | 用户服务：认证、用户管理、租户管理、菜单、角色 |
| `cloud-account-lzlj-user` | LZLJ 用户服务：定制化用户功能 |
| `cloud-account-lzlj-entrance` | LZLJ 入口层父模块 |
| `cloud-account-lzlj-gateway` | 网关：路由、鉴权、限流 |

---

## 三、包命名规范

### 3.1 顶级包

```
com.lzlj.account.*
```

**禁止使用**：
- `com.lzlj.store` （已废弃）
- `com.lzlj.lzlj` （重复顶级包）

### 3.2 业务模块包

```
com.lzlj.account.{模块名}.*
```

| 模块 | 包名 | 端口 |
|------|------|------|
| cloud-account-lzlj-gateway | `com.lzlj.account.gateway` | 28080 |
| cloud-account-lzlj-auth | `com.lzlj.account.user` | 9294 |
| cloud-account-lzlj-user | `com.lzlj.account.user` | 9093 |

### 3.3 包内结构

```
com.lzlj.account.{模块名}.{模块名}/
├── controller/    # 控制层
├── service/       # 服务层
├── dao/           # 数据访问层
├── entity/        # 实体类（MyBatis 映射）
├── dto/           # 数据传输对象（入参和出参统一使用）
├── mapper/        # MyBatis Mapper 接口
├── config/        # 配置类
├── handler/       # 处理器
└── log/           # 日志相关（操作日志、API日志）
```

---

## 四、服务接口契约

### 4.1 服务间调用 = FeignClient

```
┌─────────────────────────────────────────────────────────┐
│                     服务 A (调用方)                       │
│                                                          │
│   GoodsController                                        │
│        │                                                 │
│        ▼                                                 │
│   ┌─────────────────────────────────┐                   │
│   │  FeignClient (接口契约)          │                   │
│   │  = 服务间接口                    │                   │
│   └─────────────────────────────────┘                   │
└─────────────────────────────────────────────────────────┘
                          │
                          │ HTTP 调用
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     服务 B (被调用方)                     │
│                                                          │
│   ┌─────────────────────────────────┐                   │
│   │  FeignClient (接口契约)          │                   │
│   │  + UserFeignClient              │                   │
│   └─────────────────────────────────┘                   │
│                    │                                     │
│                    ▼                                     │
│             UserController                               │
└─────────────────────────────────────────────────────────┘
```

### 4.2 FeignClient 定义规范

```java
@FeignClient(
    name = "lzlj-auth",         // Nacos 注册的服务名
    path = "/user"              // Controller 的 @RequestMapping
)
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);

    @GetMapping("/current")
    Result<UserDTO> getCurrentUser();
}
```

### 4.3 FeignClient 约束

| 规则 | 说明 |
|------|------|
| 必须在 `cloud-account-common-api` 或 `cloud-account-lzlj-api` 模块定义 | 供调用方依赖 |
| 返回类型使用 `Result<T>` 统一封装 | 全局异常处理 |
| DTO 必须实现 `Serializable` | 跨服务传输 |
| DTO 放在 common-api 模块 | 被调用方和调用方共享 |

---

## 五、DTO 规范

### 5.1 共享层级

```
┌─────────────────────────────────────────────────────┐
│           cloud-account-common-api /                 │
│              cloud-account-lzlj-api                  │
│                                                      │
│   feign/                                             │
│   ├── UserFeignClient.java          # Feign 接口     │
│   └── dto/                                          │
│       └── UserDTO.java             # 跨服务共享 DTO  │
│                                                      │
│   core/                                             │
│   └── Result.java                 # 统一响应封装    │
└─────────────────────────────────────────────────────┘
                          ▲
                          │ 被调用
                          │
┌─────────────────────────────────────────────────────┐
│           cloud-account-lzlj-biz                    │
│                                                      │
│   {模块名}/                                         │
│   ├── dto/                      # 统一 DTO         │
│   └── entity/                   # 本地 Entity       │
└─────────────────────────────────────────────────────┘
```

### 5.2 DTO 统一约定

**不再区分 VO，所有数据传输统一使用 DTO**。

| 类型 | 位置 | 说明 |
|------|------|------|
| Entity | `{模块}/entity/` | MyBatis-Plus 数据库映射，仅本地使用 |
| DTO | `{模块}/dto/` 或 `common-api/dto/` | 统一数据传输，包括内部和跨服务 |

**原因**：难以预判接口是否会跨服务共享，统一使用 DTO 减少复杂度。

### 5.3 DTO 分布规则

| DTO 用途 | 位置 | 例子 |
|---------|------|------|
| 跨服务共享 | `common-api/dto/` 或 `lzlj-api/dto/` | `UserDTO`、`OrderDTO` |
| 本地使用 | `{模块}/dto/` | `GoodsCreateDTO`、`GoodsQueryDTO` |

### 5.4 Entity 规范

**Entity 属于本地服务，不放 common**。

```
# 正确
cloud-account-lzlj-biz/cloud-account-lzlj-auth/
└── entity/
    └── UserEntity.java    # 本地 Entity

# 禁止
cloud-account-common/
└── entity/                # 禁止！Entity 不共享
```

**原因**：Entity 紧耦合数据库 schema，跨服务共享会导致强耦合。

---

## 六、服务内部结构

### 6.1 分层

```
{模块}/
├── controller/     # 接收请求，参数校验
├── service/       # 业务逻辑
├── dao/           # 数据访问（MyBatis Mapper）
├── mapper/        # MyBatis Mapper 接口
├── dto/           # 统一数据传输（不再区分 VO）
├── entity/        # 本地 Entity
└── config/        # 配置类
```

### 6.2 Service 规范

| 场景 | 规范 |
|------|------|
| 单实现，无扩展需求 | 直接使用实现类 `@Service` |
| 需要 mock 测试 | 抽取接口 |
| 需要策略替换 | 接口 + 多实现 |
| 需要运行时扩展 | 事件驱动 / 策略模式 |

**示例：简单场景**

```java
@Service
public class GoodsService {
    public void create(GoodsDTO dto) {
        // 直接实现
    }
}
```

**示例：需要扩展**

```java
// 事件驱动扩展
@Service
public class GoodsService {
    public void create(GoodsDTO dto) {
        // 原逻辑
        eventPublisher.publish(new GoodsCreatedEvent(dto));
    }
}

@Component
public class GoodsExtensionListener {
    @EventListener
    public void onGoodsCreated(GoodsCreatedEvent event) {
        // 扩展逻辑：发消息、更新缓存等
    }
}
```

---

## 七、依赖关系

### 7.1 允许的依赖

```
controller → service → dao/mapper
                  ↓
              common-api (FeignClient)
                  ↓
              common-core (Result, Exception)
```

### 7.2 禁止的依赖

```
❌ 服务 A → 服务 B → 服务 A          # 循环依赖
❌ 服务 A → 服务 B 的 entity         # 强耦合
❌ 服务 A → common 的 entity          # 不允许
```

### 7.3 依赖方向图

```
                    ┌─────────────────┐
                    │ cloud-account-  │
                    │ common-api      │  ← FeignClient 定义
                    │ cloud-account-  │
                    │ common-core     │  ← Result, Exception
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
   │cloud-account│    │cloud-account│    │cloud-account│
   │ -lzlj-auth  │    │ -lzlj-user  │    │ -lzlj-api   │
   │  (用户服务)  │    │  (用户服务)  │    │  (API定义)   │
   └─────┬──────┘    └─────┬──────┘    └─────┬──────┘
         │                   │                   │
         │   FeignClient    │                   │
         └───────────────────┴───────────────────┘
```

---

## 八、缓存策略

### 8.1 缓存组件

| 组件 | 模块 | 说明 |
|------|------|------|
| `@Cacheable` | `cloud-account-common-redis` | 声明式缓存，基于 Redisson |
| `UserCacheService` | `cloud-account-lzlj-auth` | 旁路缓存 Demo 示例 |

### 8.2 @Cacheable（声明式缓存）

**优点**：简单，注解即可
**缺点**：同步阻塞，无法精细控制

```java
@Cacheable(value = "apiKeyAuth", key = "#apiKey", unless = "#result == null")
public ApiKeyAuthDTO getAuthInfoByApiKey(String apiKey) {
    // 缓存未命中时执行
}
```

### 8.3 旁路缓存（Cache-Aside）

**优点**：完全可控，支持复杂场景
**缺点**：代码量大

```java
// 读：先缓存 -> 未命中 -> 查DB -> 写入缓存
public UserDTO getById(Long id) {
    UserDTO cached = cache.get(id);
    if (cached != null) return cached;
    UserDTO user = db.findById(id);
    cache.put(id, user);
    return user;
}

// 写：先DB -> 删除缓存（不是更新）
public void update(User user) {
    db.update(user);
    cache.remove(user.getId());
}
```

**Demo 位置**：`cloud-account-lzlj-biz/cloud-account-lzlj-auth/.../service/impl/UserCacheService.java`

### 8.4 缓存策略选择

| 场景 | 推荐 |
|------|------|
| 简单查询、低并发 | `@Cacheable` |
| 高并发、复杂逻辑 | 旁路缓存 |
| 缓存一致性要求高 | 旁路缓存 + 延迟双删 |
| 快速开发 | `@Cacheable` |

---

## 九、约束汇总

| 编号 | 约束 | 说明 |
|------|------|------|
| C1 | 包前缀 `com.lzlj.account.{模块名}` | 禁止 `com.lzlj.store`、`com.lzlj.lzlj` |
| C2 | FeignClient 定义在 `cloud-account-common-api` 或 `cloud-account-lzlj-api` | 服务间接口契约 |
| C3 | FeignClient 依赖的 DTO 放 `cloud-account-common-api` | 跨服务共享 |
| C4 | Entity 不放 common | 本地紧耦合数据库 |
| C5 | 服务间单向依赖 | 禁止循环依赖 |
| C6 | Entity 不跨服务传递 | 使用 DTO 代替 |
| C7 | 返回值统一 `Result<T>` | 全局异常处理 |
| C8 | Service 简单优先 | 无需强制接口 |
| C9 | 扩展用事件/策略 | 避免修改原类 |
| C10 | DTO 实现 `Serializable` | 跨服务传输 |

---

## 十、违规示例

### 10.1 包命名违规

```java
// ❌ 错误
package com.lzlj.store.user;

// ✅ 正确
package com.lzlj.account.user;
```

### 10.2 循环依赖

```java
// ❌ 禁止：服务 A 依赖服务 B，服务 B 又依赖服务 A
// goods-service 调用 user-service
// user-service 又调用 goods-service
```

### 10.3 Entity 跨服务

```java
// ❌ 禁止：goods-service 引用 user-service 的 Entity
public class GoodsService {
    private final UserEntity userEntity;  // 不允许！
}

// ✅ 正确：使用 DTO
public class GoodsService {
    private final UserDTO userDTO;  // 允许
}
```

### 10.4 DTO 命名

```java
// ❌ 错误：缩写、匈牙利命名
private UserDTO userdto;
private UserDTO mUser;

// ✅ 正确：清晰命名
private UserDTO userDTO;
private UserDTO currentUser;
```

---

## 十一、违反检查

```bash
# 检查是否使用废弃包名
grep -r "com\.lzlj\.store\|com\.lzlj\.lzlj" --include="*.java" .

# 检查 Entity 是否在 common
grep -r "package com\.lzlj\.account\.common.*\.entity" --include="*.java" .

# 检查循环依赖（需要人工审查）
# 原则：goods 不应被 user 依赖
```
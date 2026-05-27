# LZLJ Cloud 架构规范

## 一、模块结构

```
lzlj-cloud/
├── account-common/              # 公共模块
│   ├── account-common-core/     # 核心公共代码
│   └── account-common-api/      # Feign 接口定义
├── account-biz-saas/            # SaaS 业务模块
│   ├── account-saas-auth/       # 用户服务
│   └── account-saas-goods/      # 商品服务
├── account-biz-lzlj/            # LZLJ 定制业务模块
│   └── account-lzlj-user/       # LZLJ 用户服务
└── account-gateway/            # API 网关
```

---

## 二、包命名规范

### 2.1 顶级包

```
com.lzlj.account.*
```

**禁止使用**：
- `com.lzlj.store` （已废弃）
- `com.lzlj.lzlj` （重复顶级包）

### 2.2 业务模块包

```
com.lzlj.account.{模块名}.*
```

| 模块 | 包名 | 端口 |
|------|------|------|
| account-gateway | `com.lzlj.account.gateway` | 18080 |
| account-saas-auth | `com.lzlj.account.user` | 9092 |
| account-saas-goods | `com.lzlj.account.goods` | 9091 |
| account-lzlj-user | `com.lzlj.account.user` | 9093 |

### 2.3 包内结构

```
com.lzlj.account.{模块名}.{模块名}/
├── controller/    # 控制层
├── service/       # 服务层
├── dao/          # 数据访问层
├── entity/       # 实体类
├── dto/          # 数据传输对象
├── vo/           # 视图对象
├── config/       # 配置类
└── handler/      # 处理器
```

---

## 三、服务接口契约

### 3.1 服务间调用 = FeignClient

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

### 3.2 FeignClient 定义规范

```java
@FeignClient(
    name = "saas-auth",           // Nacos 注册的服务名
    path = "/user"                 // Controller 的 @RequestMapping
)
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);

    @GetMapping("/current")
    Result<UserDTO> getCurrentUser();
}
```

### 3.3 FeignClient 约束

| 规则 | 说明 |
|------|------|
| 必须在 `account-common-api` 模块定义 | 供调用方依赖 |
| 返回类型使用 `Result<T>` 统一封装 | 全局异常处理 |
| DTO 必须实现 `Serializable` | 跨服务传输 |
| DTO 放在 common-api 模块 | 被调用方和调用方共享 |

---

## 四、DTO/VO 规范

### 4.1 共享层级

```
┌─────────────────────────────────────────────────────┐
│              account-common-api                      │
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
│              account-biz-*                          │
│                                                      │
│   {模块名}/                                         │
│   ├── dto/                      # 本地 DTO         │
│   ├── vo/                       # 本地 VO           │
│   └── entity/                   # 本地 Entity       │
└─────────────────────────────────────────────────────┘
```

### 4.2 DTO 分布规则

| DTO 类型 | 位置 | 例子 |
|---------|------|------|
| 跨服务共享 | `common-api/dto/` | `UserDTO`、`OrderDTO` |
| 本地使用 | `{模块}/dto/` | `GoodsCreateDTO` |
| 本地 VO | `{模块}/vo/` | `GoodsVO` |

### 4.3 Entity 规范

**Entity 属于本地服务，不放 common**。

```
# 正确
account-saas-goods/
└── entity/
    └── GoodsEntity.java    # 本地 Entity

# 禁止
account-common/
└── entity/                # 禁止！Entity 不共享
```

**原因**：Entity 紧耦合数据库 schema，跨服务共享会导致强耦合。

---

## 五、服务内部结构

### 5.1 分层

```
{模块}/
├── controller/     # 接收请求，参数校验
├── service/       # 业务逻辑
├── dao/           # 数据访问
├── dto/           # 本地 DTO
├── vo/            # 本地 VO
└── entity/        # 本地 Entity
```

### 5.2 Service 规范

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

## 六、依赖关系

### 6.1 允许的依赖

```
controller → service → dao
                  ↓
              common-api (FeignClient)
                  ↓
              common-core (Result, Exception)
```

### 6.2 禁止的依赖

```
❌ 服务 A → 服务 B → 服务 A          # 循环依赖
❌ 服务 A → 服务 B 的 entity         # 强耦合
❌ 服务 A → common 的 entity          # 不允许
```

### 6.3 依赖方向图

```
                    ┌─────────────────┐
                    │ account-common   │
                    │  - common-api    │  ← FeignClient 定义
                    │  - common-core    │  ← Result, Exception
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
   │saas-auth   │    │saas-goods  │    │lzlj-user   │
   │(用户服务)   │    │(商品服务)   │    │(用户服务)   │
   └─────┬──────┘    └─────┬──────┘    └─────┬──────┘
         │                   │                   │
         │   FeignClient    │                   │
         └───────────────────┴───────────────────┘
```

---

## 七、约束汇总

| 编号 | 约束 | 说明 |
|------|------|------|
| C1 | 包前缀 `com.lzlj.account.{模块名}` | 禁止 `com.lzlj.store`、`com.lzlj.lzlj` |
| C2 | FeignClient 定义在 `common-api` | 服务间接口契约 |
| C3 | FeignClient 依赖的 DTO 放 `common-api` | 跨服务共享 |
| C4 | Entity 不放 common | 本地紧耦合数据库 |
| C5 | 服务间单向依赖 | 禁止循环依赖 |
| C6 | Entity 不跨服务传递 | 使用 DTO 代替 |
| C7 | 返回值统一 `Result<T>` | 全局异常处理 |
| C8 | Service 简单优先 | 无需强制接口 |
| C9 | 扩展用事件/策略 | 避免修改原类 |
| C10 | DTO 实现 `Serializable` | 跨服务传输 |

---

## 八、违规示例

### 8.1 包命名违规

```java
// ❌ 错误
package com.lzlj.store.user;

// ✅ 正确
package com.lzlj.account.user;
```

### 8.2 循环依赖

```java
// ❌ 禁止：服务 A 依赖服务 B，服务 B 又依赖服务 A
// goods-service 调用 user-service
// user-service 又调用 goods-service
```

### 8.3 Entity 跨服务

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

### 8.4 DTO 命名

```java
// ❌ 错误：缩写、匈牙利命名
private UserDTO userdto;
private UserDTO mUser;

// ✅ 正确：清晰命名
private UserDTO userDTO;
private UserDTO currentUser;
```

---

## 九、违反检查

```bash
# 检查是否使用废弃包名
grep -r "com\.lzlj\.store\|com\.lzlj\.lzlj" --include="*.java" .

# 检查 Entity 是否在 common
grep -r "package com\.lzlj\.account\.common.*\.entity" --include="*.java" .

# 检查循环依赖（需要人工审查）
# 原则：goods 不应被 user 依赖
```

# Feign 降级方案文档

## 概述

在微服务架构中，服务调用者需要具备容错能力，当被调用的服务不可用时，能够自动降级返回一个兜底数据，保证业务可用。

本文档介绍两种 Feign 降级方案的实现方式、优缺点及适用场景。

---

## 方案一：Feign Fallback（原生方案）

### 实现方式

#### 1. 定义 Fallback 类

```java
@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public Result<UserFeignClient.UserInfo> getById(Long id) {
        log.warn("Feign调用用户服务失败: getById({})", id);
        return Result.fail("用户服务暂时不可用");
    }

    @Override
    public Result<UserInfo> getCurrentUser() {
        log.warn("Feign调用用户服务失败: getCurrentUser()");
        return Result.fail("用户服务暂时不可用");
    }
}
```

#### 2. 在 @FeignClient 中指定 fallback

```java
@FeignClient(
        name = "saas-auth",
        path = "/user",
        fallback = UserFeignClientFallback.class
)
public interface UserFeignClient {
    @GetMapping("/{id}")
    Result<UserInfo> getById(@PathVariable("id") Long id);

    @GetMapping("/current")
    Result<UserInfo> getCurrentUser();
}
```

#### 3. 确保启动类扫描到 Fallback

```java
@SpringBootApplication(scanBasePackages = {
    "com.lzlj.lzlj.goods",
    "com.lzlj.account.common"  // 必须包含 fallback 所在包
})
@EnableFeignClients(basePackages = "com.lzlj.account.common.api.feign")
public class SaasGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaasGoodsApplication.class, args);
    }
}
```

### 优缺点

| 优点 | 缺点 |
|------|------|
| 实现简单，Spring Cloud 原生支持 | 需要为每个 FeignClient 创建对应 Fallback 类 |
| 代码可控，降级逻辑清晰可见 | Fallback 在服务不可用时不一定触发（取决于 Spring Cloud 版本和 CircuitBreaker 配置） |
| 无需额外配置中心/规则 | 与 Sentinel 等高级熔断器功能不兼容 |

### 注意事项

1. **Spring Cloud 2021+ 版本问题**：原生 Fallback 在 Spring Cloud 2021.0.9 + Spring Cloud Alibaba 2021.0.5.0 版本中，当服务不可用时可能不触发 Fallback，而是抛出 `RetryableException`，被全局异常处理器捕获。

2. **解决方案**：配合 `feign.circuitbreaker.enabled: true` 使用，或采用手动降级方式。

---

## 方案二：手动降级（推荐）

### 实现方式

#### 1. 直接在调用处处理异常

```java
@Slf4j
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final UserFeignClient userFeignClient;
    private final UserFeignClientFallback fallback;  // 注入 Fallback

    @GetMapping("/test/feign/{id}")
    public Result<UserFeignClient.UserInfo> testFeignFallback(@PathVariable Long id) {
        try {
            Result<UserFeignClient.UserInfo> result = userFeignClient.getById(id);
            return result;
        } catch (Exception e) {
            log.error("Feign调用异常: type={}, message={}",
                    e.getClass().getName(), e.getMessage());
            // 手动调用 Fallback
            return fallback.getById(id);
        }
    }
}
```

#### 2. Fallback 类实现

```java
@Slf4j
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public Result<UserFeignClient.UserInfo> getById(Long id) {
        log.warn("Feign降级: getById({})", id);
        return Result.fail("用户服务暂时不可用");
    }

    @Override
    public Result<UserInfo> getCurrentUser() {
        log.warn("Feign降级: getCurrentUser()");
        return Result.fail("用户服务暂时不可用");
    }
}
```

### 优缺点

| 优点 | 缺点 |
|------|------|
| **可靠性高**：100% 触发降级 | 代码稍多（需要 try-catch） |
| **可控性强**：精确控制降级时机和逻辑 | 需要注入 Fallback 实例 |
| **无版本依赖**：不依赖 Spring Cloud/CircuitBreaker 配置 | Fallback 逻辑与业务代码耦合 |
| **调试简单**：日志清晰，降级路径明确 | - |
| **适用性广**：任何 Feign 调用场景都适用 | - |

### 适用场景

- **生产环境**：追求稳定性，要求降级逻辑 100% 可靠
- **微服务调用**：服务数量多，需要统一的降级策略
- **关键业务链路**：不允许降级失败

---

## 方案三：Sentinel 熔断降级

### 机制说明

Sentinel 是阿里巴巴开源的流量控制组件，提供限流、熔断、降级能力。通过 Sentinel 可以实现更细粒度的流量控制。

### 实现方式

#### 1. 启用 Sentinel Feign 支持

```yaml
feign:
  sentinel:
    enabled: true

sentinel:
  transport:
    port: 8719
  datasource:
    ds:
      nacos:
        server-addr: 127.0.0.1:8848
        data-id: sentinel-saas-goods.json
        group-id: DEFAULT_GROUP
        data-type: json
        rule-type: flow
```

#### 2. 定义 BlockHandler（降级处理）

BlockHandler 必须是 `static` 方法：

```java
@Slf4j
@Component
public class UserFeignBlockHandler {

    public static Result<UserFeignClient.UserInfo> getByIdBlockHandler(Long id, Throwable t) {
        log.warn("Sentinel降级[getById]: id={}, 原因={}", id, t.getMessage());
        return Result.fail("用户服务暂时不可用");
    }

    public static Result<UserFeignClient.UserInfo> getCurrentUserBlockHandler(Throwable t) {
        log.warn("Sentinel降级[getCurrentUser]: 原因={}", t.getMessage());
        return Result.fail("用户服务暂时不可用");
    }
}
```

#### 3. 使用 @SentinelResource 注解

```java
@GetMapping("/test/sentinel/{id}")
@SentinelResource(value = "userFeign#getById",
        fallbackClass = UserFeignBlockHandler.class,
        fallback = "getByIdBlockHandler")
public Result<UserFeignClient.UserInfo> testSentinelFallback(@PathVariable Long id) {
    return userFeignClient.getById(id);
}
```

#### 4. Nacos 配置 Sentinel 熔断规则

在 Nacos 中创建 `sentinel-saas-goods.json` 配置：

```json
[
  {
    "resource": "userFeign#getById",
    "count": 0,
    "grade": 1,
    "time": 1,
    "msg": "用户服务暂时不可用"
  }
]
```

参数说明：
- `resource`: 资源名，必须与 `@SentinelResource(value = "...")` 一致
- `count`: 阈值（0 表示开启熔断）
- `grade`: 熔断策略（0=异常比例，1=秒级 RT）
- `time`: 熔断时长（秒）
- `msg`: 降级提示信息

### 优缺点

| 优点 | 缺点 |
|------|------|
| 功能强大：支持限流、熔断、降级一体化 | 配置复杂，需要理解 Sentinel 规则 |
| 可通过 Dashboard 动态调整规则 | 需要额外部署 Sentinel Dashboard |
| 支持慢调用比例、异常比例等多种熔断策略 | 学习成本较高 |
| 可视化监控：提供实时监控面板 | - |

### 适用场景

- **高并发场景**：需要限流保护
- **需要可视化管控**：通过 Dashboard 动态调整规则
- **复杂熔断策略**：基于慢调用比例、异常比例等多种策略

---

## 方案对比

| 特性 | Feign Fallback | 手动降级 | Sentinel |
|------|---------------|----------|----------|
| **实现复杂度** | 低 | 中 | 高 |
| **可靠性** | 中（依赖版本） | 高 | 高 |
| **可控性** | 中 | 高 | 中 |
| **降级时机** | 服务不可用时 | 任意位置 | 限流/熔断触发时 |
| **规则配置** | 代码 | 代码 | Nacos/Dashboard |
| **监控能力** | 无 | 无 | 完整监控 |
| **学习成本** | 低 | 低 | 高 |

---

## 推荐方案

### 生产环境推荐：手动降级

考虑到可靠性优先，推荐使用**手动降级方案**：

```java
try {
    return userFeignClient.getById(id);
} catch (Exception e) {
    log.warn("Feign调用失败，降级处理: {}", e.getMessage());
    return Result.fail("用户服务暂时不可用");
}
```

### 进阶方案：Sentinel

如果系统已经有 Sentinel 基础设施，或需要限流+熔断一体化能力，建议使用 Sentinel 方案。

---

## 测试验证

### 测试降级是否生效

```bash
# 1. 正常调用（服务可用）
curl http://localhost:18080/api/saas-goods/goods/test/feign/1
# 预期：返回正常用户数据

# 2. kill 掉被调用服务后再次调用
curl http://localhost:18080/api/saas-goods/goods/test/feign/1
# 预期：返回降级响应 {"code":400,"message":"用户服务暂时不可用","success":false}
```

---

## 配置参考

### Maven 依赖

```xml
<!-- OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Sentinel（可选） -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<!-- LoadBalancer -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### 配置文件

```yaml
feign:
  # 启用 Sentinel CircuitBreaker（可选）
  circuitbreaker:
    enabled: true
  # 启用 Sentinel 降级（使用 Sentinel 时开启）
  sentinel:
    enabled: false
```

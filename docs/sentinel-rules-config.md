# Sentinel 熔断规则配置

## 说明

如果只需要 `@SentinelResource fallback` 降级功能，**不需要**在 Nacos 配置任何规则。Sentinel 会自动在方法抛出异常时触发 fallback。

以下配置仅在需要以下场景时使用：
- 限流规则（QPS 控制）
- 熔断规则（基于异常比例/慢调用比例）
- 动态规则调整（通过 Sentinel Dashboard）

## 参数说明

| 参数 | 说明 |
|------|------|
| `resource` | 资源名（与 `@SentinelResource(value = "...")` 保持一致） |
| `count` | 阈值（限流=QPS，熔断=比例值） |
| `grade` | 熔断策略（0=异常比例，1=秒级 RT） |
| `time` | 熔断持续时长（秒） |
| `msg` | 降级提示信息 |

## grade 可选值

| 值 | 策略 | 说明 |
|-----|------|------|
| 0 | 异常比例 | 请求异常比例达到阈值时熔断 |
| 1 | 秒级 RT | 平均响应时间超过阈值时熔断 |

## 配置示例

### 1. 限流规则 - 每秒最多 10 个请求

```json
[
  {
    "resource": "userFeign#getById",
    "count": 10,
    "grade": 1,
    "time": 1
  }
]
```

### 2. 熔断规则 - 异常比例 > 50% 时熔断 5 秒

```json
[
  {
    "resource": "userFeign#getById",
    "count": 0.5,
    "grade": 0,
    "time": 5,
    "msg": "用户服务暂时不可用"
  }
]
```

### 3. 慢调用比例熔断 - 超过 1000ms 的调用视为慢调用，比例 > 50% 时熔断

```json
[
  {
    "resource": "userFeign#getById",
    "count": 0.5,
    "grade": 1,
    "time": 10,
    "slowRatioThreshold": 1000
  }
]
```

## Context

### 现状

```
cloud-account-saas/
├── cloud-account-saas-biz/
│   ├── cloud-account-saas-biz-auth/     ← SaaS 认证服务（业务逻辑）
│   └── cloud-account-saas-biz-task/     ← 实际是 goods 商品服务，待改造
│       ├── goods/                        ← 商品相关代码（需删除）
│       └── pom.xml                       ← 已有 xxl-job 依赖
└── cloud-account-saas-api/              ← Feign API 定义
```

`saas-biz-task` 模块：
- 模块名 `cloud-account-saas-goods`，文件夹 `saas-biz-task`
- 已有 xxl-job 依赖
- 排除了数据库（无业务数据）
- 没有任何 ScheduleTask 实现

### 目标架构

```
cloud-account-saas/
├── cloud-account-saas-biz/
│   ├── cloud-account-saas-biz-auth/      ← SaaS 认证服务
│   │   └── internal/                      ← 新增：Internal*Controller
│   └── cloud-account-saas-biz-task/      ← 改造为任务调度服务
│       ├── schedule/                      ← 新增：ScheduleTask 实现
│       ├── feign/                          ← 新增：Feign Client 调用 saas-auth
│       └── SaasTaskApplication.java        ← 重写启动类
└── cloud-account-saas-api/               ← 新增 Feign Client 定义
```

**调度流程：**
```
xxl-job admin (调度中心)
    │
    │ HTTP 调用
    ▼
saas-task (执行器)
    │
    │ ScheduleTask.execute()
    │ Feign HTTP 调用
    ▼
saas-auth (业务服务)
    │ /internal/* 接口
    ▼
执行业务逻辑
```

## Goals / Non-Goals

**Goals:**
- 将 `saas-biz-task` 改造成独立的定时任务调度服务
- 复用 `cloud-account-common-schedule` 的 xxl-job adapter 模式
- task 不承载任何业务逻辑，只负责调度
- 所有业务调用通过 Feign 远程执行

**Non-Goals:**
- 不在本阶段迁移 lzlj 侧的定时任务（先完成 SaaS 侧）
- 不修改 saas-auth 的现有业务逻辑
- 不在本阶段实现具体的定时任务（先搭框架）

## Decisions

### Decision 1: 模块命名统一

**选择**：模块名改为 `cloud-account-saas-task`，artifactId 保持 `cloud-account-saas-goods`（避免修改 parent pom）

**替代方案**：
- 重命名 artifactId → 需要修改 parent pom，改动大
- 保持现状 → 命名不一致，容易混淆

### Decision 2: Feign Client 定义位置

**选择**：Feign Client 定义放在 `cloud-account-saas-api-auth` 模块

**替代方案**：
- 放在 `cloud-account-common-api` → 公共模块不应该包含业务相关的 Feign 接口
- 新建独立 api 模块 → 增加复杂度

### Decision 3: internal 接口路径前缀

**选择**：`/internal/*`

**替代方案**：
- `/task/*` → 语义不够明确
- `/api/internal/*` → 路径过长

### Decision 4: task 模块不连接数据库

**选择**：保持现状，task 模块无数据库连接

**理由**：
- task 只负责调度，不存储业务数据
- 避免数据库连接池浪费

### Decision 5: 初始定时任务

**选择**：框架搭建完成后，实现以下初始任务：

| 任务名 | 说明 |
|--------|------|
| `menu-refresh` | 刷新菜单缓存 |
| `role-refresh` | 刷新角色缓存 |
| `dict-refresh` | 刷新数据字典缓存 |

**理由**：这些是 SaaS 最常用的基础数据，缓存刷新是定时任务的典型场景。

## Risks / Trade-offs

| Risk | Mitigation |
|------|-----------|
| Feign 调用失败导致任务失败 | 已在 FeignClient 中配置 Fallback |
| 远程调用延迟影响 xxl-job 任务执行时间 | 使用异步执行或设置合理超时 |
| internal 接口被外部非法调用 | 使用 `@InnerApi` 注解标识，内部调用校验 |

## Migration Plan

### Phase 1: 框架搭建（本阶段）

1. 清空 `saas-biz-task` 中的 goods 代码
2. 重写启动类 `SaasTaskApplication.java`
3. 配置 `cloud-account-common-schedule` 的 xxl-job adapter
4. 创建 `InternalCacheController` 暴露缓存刷新接口
5. 创建 Feign Client 定义
6. 实现 `CacheRefreshTask` 定时任务

### Phase 2: 测试验证

1. 启动 xxl-job admin
2. 启动 saas-task，执行一个简单任务
3. 验证 Feign 调用链路

## Open Questions

1. **saas-auth 的 internal 接口是否需要单独认证？**
   - 当前 xxl-job 触发 task 后，task 调用 saas-auth 无需额外认证
   - 是否需要设置 task 专用 token？

2. **是否需要 saas-auth 向 saas-task 提供接口？**
   - 当前是 saas-task 调用 saas-auth
   - 如果未来需要反向调用，需增加反向 Feign Client

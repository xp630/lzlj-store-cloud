## Why

当前定时任务分散在各业务模块中（lzlj-auth 等），每个业务模块都需要依赖 xxl-job executor，造成：
1. 多模块重复依赖 xxl-job
2. 调度逻辑散落各处，难以统一管理
3. 业务模块职责不清晰（业务 + 调度混在一起）

将调度能力抽取到独立 task 模块，各业务模块只暴露 internal HTTP 接口，task 通过 Feign 远程调用。

## What Changes

1. **改造 `saas-biz-task` 模块**
   - 清空 goods 商品相关代码
   - 改造为独立的定时任务调度服务
   - 集成 `cloud-account-common-schedule`（xxl-job adapter）
   - 通过 Feign 调用 `saas-auth` 的 internal 接口

2. **saas-auth 暴露 internal 接口**
   - 新增 `Internal*Controller` 暴露内部调度接口
   - 提供菜单、角色、数据字典缓存刷新等接口

3. **新建 Feign Client**
   - 在 `cloud-account-saas-api` 中定义 task 需要的 Feign 接口

## Capabilities

### New Capabilities

- `saas-task-service`: SaaS 定时任务调度服务，承载所有 SaaS 侧的定时任务，通过 Feign 调用业务接口执行

### Modified Capabilities

- 无（目前 SaaS 侧无定时任务，本次为全新建设）

## Impact

- **新增模块**：无（改造现有 `saas-biz-task`）
- **被改造模块**：`cloud-account-saas-goods` → `cloud-account-saas-task`
- **被调用方**：`saas-auth` 需要新增 internal 接口
- **依赖变化**：`saas-auth` 无变化，`saas-task` 新增 `cloud-account-common-schedule` 依赖

## Why

当前 LZLJ 的调度任务使用硬编码的 XXL-JOB，如果未来需要更换调度框架（如 PowerJob、Quartz 等），迁移成本高。需要抽象调度接口，与具体实现解耦。

## What Changes

- 新增 `cloud-account-common/cloud-account-common-schedule` 模块
- 抽象 `ScheduleTask` 接口，定义任务执行规范
- 提供 XXL-JOB 适配实现 `XxlJobScheduleAdapter`
- 重构 `SaasSyncJobHandler` 为 `SaasSyncTask`，实现 `ScheduleTask` 接口
- 统一任务调度配置，支持切换调度框架

## Capabilities

### New Capabilities
- `schedule-common`: 调度任务公共抽象层，定义 `ScheduleTask` 接口和基础组件

## Impact

- 新增 `cloud-account-common-schedule` 模块
- 现有 `lzlj-auth` 模块的 `XxlJobConfig` 和 `SaasSyncJobHandler` 需要重构

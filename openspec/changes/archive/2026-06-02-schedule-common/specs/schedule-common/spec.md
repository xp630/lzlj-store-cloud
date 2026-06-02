## ADDED Requirements

### Requirement: ScheduleTask Interface
系统 SHALL 提供 `ScheduleTask` 接口，定义调度任务的统一规范。接口必须包含：
- `name()`: 返回任务名称，用于任务分发
- `execute()`: 执行任务逻辑，返回执行结果描述

### Requirement: XxlJobScheduleAdapter
系统 SHALL 提供 XXL-JOB 适配器 `XxlJobScheduleAdapter`，实现以下功能：
- 实现 XXL-JOB 的 `@XxlJob` 入口方法
- 根据任务名分发到对应的 `ScheduleTask` 实现
- 任务参数通过 XXL-JOB 的 JobParam 传递

### Requirement: Schedule Configuration
系统 SHALL 提供调度框架配置，包含：
- 调度框架类型（当前为 XXL-JOB）
- 调度服务器地址
- 执行器配置（端口、日志路径等）

### Requirement: Task Registration
业务任务实现 `ScheduleTask` 接口后 SHALL 自动注册到适配器，适配器 SHALL 在启动时扫描所有 `ScheduleTask` 实现并注册任务名。

### Requirement: Task Execution Result
`execute()` 方法 SHALL 返回任务执行结果的描述字符串，系统 SHALL 将该字符串记录到 XXL-JOB 日志中。

#### Scenario: Execute registered task
- **WHEN** XXL-JOB 触发任务 "saas-sync"
- **THEN** 适配器 SHALL 找到 name="saas-sync" 的 ScheduleTask 并调用其 execute() 方法
- **AND** 返回值 SHALL 被记录到 XXL-JOB 执行日志

#### Scenario: Task not found
- **WHEN** XXL-JOB 触发不存在的任务
- **THEN** 适配器 SHALL 返回 "Task not found: {taskName}"
- **AND** 任务状态 SHALL 标记为失败

#### Scenario: Task execution exception
- **WHEN** ScheduleTask.execute() 抛出异常
- **THEN** 适配器 SHALL 捕获异常并返回错误信息
- **AND** XXL-JOB SHALL 标记任务执行失败

## 1. 创建 Common Schedule 模块

- [x] 1.1 在 `cloud-account-common/` 下创建 `cloud-account-common-schedule` 子模块
- [x] 1.2 添加 pom.xml，依赖 `cloud-account-common-core` 和 `xxl-job-core`
- [x] 1.3 创建包结构 `com.lzlj.account.common.schedule`

## 2. 实现 ScheduleTask 接口

- [x] 2.1 创建 `ScheduleTask` 接口，定义 `name()` 和 `execute()` 方法
- [x] 2.2 创建 `ScheduleTaskRegistry` 用于注册和查找任务

## 3. 实现 XXL-JOB 适配器

- [x] 3.1 创建 `XxlJobScheduleAdapter` 类
- [x] 3.2 实现 `@XxlJob("scheduleTaskExecutor")` 入口方法
- [x] 3.3 实现任务分发逻辑：根据任务名查找并调用对应的 ScheduleTask
- [x] 3.4 实现异常处理和日志记录

## 4. 实现 ScheduleConfig 配置类

- [x] 4.1 创建 `ScheduleConfig` 配置类，配置 XXL-JOB Executor
- [x] 4.2 从 Nacos 读取 `xxl.job.*` 配置

## 5. 重构业务任务

- [x] 5.1 将 `SaasSyncJobHandler` 重构为 `SaasSyncTask`，实现 `ScheduleTask` 接口
- [x] 5.2 删除 `lzlj-auth` 中的 `XxlJobConfig`（已移入 common-schedule）
- [x] 5.3 删除 `lzlj-auth` 中的 `job` 包（已迁移到 `schedule`）

## 6. 更新依赖和配置

- [x] 6.1 在 `lzlj-auth` 的 pom.xml 中添加 `cloud-account-common-schedule` 依赖
- [ ] 6.2 验证 Nacos 配置正确加载

## 7. 验证测试

- [ ] 7.1 重启 lzlj-auth 服务
- [ ] 7.2 在 XXL-JOB 控制台验证任务可正常触发
- [ ] 7.3 验证任务执行日志正确记录

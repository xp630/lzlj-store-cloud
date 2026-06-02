## Context

当前 LZLJ 直接在 `lzlj-auth` 模块中引入 XXL-JOB 的 `XxlJobSpringExecutor` 和 `@XxlJob` 注解实现调度任务。存在以下问题：

1. **耦合严重**: 业务代码直接依赖 XXL-JOB 注解和类
2. **替换成本高**: 如需更换调度框架（如 PowerJob、Quartz），需要改写所有任务类
3. **测试困难**: 业务逻辑与 XXL-JOB 框架紧耦合，单元测试需要模拟 JobHandler

## Goals / Non-Goals

**Goals:**
- 抽象调度任务接口 `ScheduleTask`，定义任务执行规范
- 实现 XXL-JOB 适配器 `XxlJobScheduleAdapter`，将 XXL-JOB 作为具体实现
- 通过适配器模式，未来可无缝切换到其他调度框架
- 统一调度任务的配置管理

**Non-Goals:**
- 不实现多调度框架同时运行
- 不迁移现有 XXL-JOB 的管理功能（如任务日志、任务依赖等高级特性）

## Decisions

### 1. 抽象 `ScheduleTask` 接口

```java
public interface ScheduleTask {
    /**
     * 任务名称
     */
    String name();

    /**
     * 执行任务
     * @return 执行结果描述
     */
    String execute();
}
```

**替代方案**: 使用注解 + 反射，但接口更清晰，也方便扩展。

### 2. 创建 `cloud-account-common-schedule` 模块

```
cloud-account-common/
├── cloud-account-common-core/          # 已存在
├── cloud-account-common-database/      # 已存在
├── cloud-account-common-schedule/     # 新增
│   ├── pom.xml
│   └── src/main/java/com/lzlj/account/common/schedule/
│       ├── ScheduleConfig.java        # 调度框架配置
│       ├── ScheduleTask.java          # 任务接口
│       └── adapter/
│           └── xxljob/
│               └── XxlJobScheduleAdapter.java
```

### 3. 适配器模式对接 XXL-JOB

```java
@Component
@RequiredArgsConstructor
public class XxlJobScheduleAdapter {
    private final List<ScheduleTask> tasks;

    @XxlJob("scheduleTaskExecutor")
    public String execute() {
        // 从 XXL-JOB 上下文获取任务名
        String taskName = XxlJobHelper.getJobParam();
        return tasks.stream()
            .filter(t -> t.name().equals(taskName))
            .findFirst()
            .map(ScheduleTask::execute)
            .orElse("Task not found: " + taskName);
    }
}
```

### 4. 业务任务实现 `ScheduleTask`

```java
@Component
@RequiredArgsConstructor
public class SaasSyncTask implements ScheduleTask {

    private final LzljPaymentChannelService paymentChannelService;
    private final LzljMerchantService merchantService;

    @Override
    public String name() {
        return "saas-sync";
    }

    @Override
    public String execute() {
        // 业务逻辑
    }
}
```

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| XXL-JOB 特定参数传递方式 | 通过任务名 + JSON 参数传递，业务自行解析 |
| 任务日志 / 监控丢失 | XXL-JOB 原生日志保留，仅业务日志通过统一日志框架 |
| 适配器性能开销 | 极小，Map 查找 O(1) |

## Migration Plan

1. 创建 `cloud-account-common-schedule` 模块
2. 实现 `ScheduleTask` 接口和 XXL-JOB 适配器
3. 将 `XxlJobConfig` 移入 common-schedule
4. 重构 `SaasSyncJobHandler` → `SaasSyncTask`
5. 删除 `lzlj-auth` 中的旧实现
6. 验证 XXL-JOB 控制台可正常触发任务

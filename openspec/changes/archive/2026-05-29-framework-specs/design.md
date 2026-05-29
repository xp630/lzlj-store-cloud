## Context

当前项目规范分布在：

| 文档 | 内容 |
|------|------|
| `docs/architecture-convention.md` | 模块结构、包命名、依赖方向、DTO规范、Feign规范 |
| `docs/package-convention.md` | 包命名规则、目录结构 |
| `docs/quick-start.md` | 环境搭建、新人上手 |

规范与代码实现之间缺乏强关联约束，文档更新不代表代码遵守。

## Goals / Non-Goals

**Goals:**
- 将核心框架开发规范沉淀为 OpenSpec Specs
- 规范可直接引用到 change proposal 中，作为设计决策的依据
- 每个 spec 包含具体场景（Scenario），可作为 code review 检查点

**Non-Goals:**
- 不创建代码强制校验工具（如 ArchUnit）
- 不修改现有代码（仅沉淀规范）
- 不合并 docs/ 目录（保留文档框架，细节引用 specs）

## Decisions

### Decision 1: Spec 命名规范

**选择**: `framework-{layer}` 格式

| Spec | 示例 |
|------|------|
| Controller | `framework-controller` |
| Entity | `framework-entity` |
| DTO | `framework-dto` |
| Service | `framework-service` |
| API | `framework-api` |
| Error Handling | `framework-error-handling` |
| Feign | `framework-feign` |

**理由**: 与 capability 命名风格一致，且明确为框架层规范。

### Decision 2: 规范粒度

**选择**: 每层一个 spec，按场景（Scenario）定义检查点

**理由**:
- 太细（每个类一个 spec）维护成本高
- 太粗（全部放一个 spec）难以引用
- 按层分类既清晰又实用

### Decision 3: 规范与 docs/ 的关系

**选择**:
- `docs/` 保留框架介绍性内容（新人上手）
- `openspec/specs/` 作为规范的具体定义（code review 依据）
- 未来 change 的 design.md 引用 `openspec/specs/` 作为规范来源

**理由**: docs/ 是面向新人的教程，specs/ 是开发者的契约。

## Risks / Trade-offs

- [Risk] specs 更新后与 docs/ 内容不一致 → [Mitigation] 标记 docs/ 中相关章节为"已迁移至 specs/，以 specs 为准"
- [Risk] specs 过于理论化无法落地 → [Mitigation] 每个规范必须有 Scenario，直接作为 code review 清单

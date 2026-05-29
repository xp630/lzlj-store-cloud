## Why

当前框架规范散落在 `docs/` 目录下的多份文档中（architecture-convention.md、package-convention.md、quick-start.md），存在以下问题：
- 文档分散，新人不知从何看起
- 规范与代码实现容易脱节（文档更新但代码未遵守，或代码变更但文档未同步）
- 没有版本控制，不知道何时改了哪些规范

需要将框架开发规范系统化地沉淀为 OpenSpec Specs，作为项目架构的"宪法"。

## What Changes

1. 创建 **Controller 规范** (`framework-controller`) — REST API 注解风格、@Valid 校验、Result 统一响应
2. 创建 **Entity 规范** (`framework-entity`) — BaseEntity/TenantEntity 继承规则、字段填充、@TableLogic
3. 创建 **DTO 规范** (`framework-dto`) — 命名规则、Create/Update/Response DTO 区分
4. 创建 **Service 规范** (`framework-service`) — @Transactional 场景、BusinessException 使用
5. 创建 **API 规范** (`framework-api`) — 路径命名（REST风格）、HTTP 方法、版本管理
6. 创建 **Error Handling 规范** (`framework-error-handling`) — 全局异常映射、ResultCode 枚举规范
7. 创建 **Feign 规范** (`framework-feign`) — Client 命名、路径、超时、降级

## Capabilities

### New Capabilities
- `framework-controller`: Controller 开发规范
- `framework-entity`: Entity/数据库映射规范
- `framework-dto`: DTO 命名与分层规范
- `framework-service`: Service 层开发规范
- `framework-api`: REST API 设计规范
- `framework-error-handling`: 异常处理与响应规范
- `framework-feign`: 服务间调用（Feign）规范

### Modified Capabilities
- 无

## Impact

### 新增文件
- `openspec/specs/framework-controller/spec.md`
- `openspec/specs/framework-entity/spec.md`
- `openspec/specs/framework-dto/spec.md`
- `openspec/specs/framework-service/spec.md`
- `openspec/specs/framework-api/spec.md`
- `openspec/specs/framework-error-handling/spec.md`
- `openspec/specs/framework-feign/spec.md`

### 受影响文档
- `docs/architecture-convention.md` — 可保留架构图，将规范细节引用到 specs
- `docs/package-convention.md` — 规范内容迁移到 specs
- `docs/quick-start.md` — 规范内容迁移到 specs

### 无影响范围
- 代码实现（本次只沉淀规范，不改代码）
- 已有 API 契约

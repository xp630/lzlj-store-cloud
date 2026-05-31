# Code Review Checklist - Data Permission

## 多表查询审查

当 PR 包含以下任意一项时，必须检查：

### 1. JOIN 类 SQL
- [ ] 是否涉及两个或以上 **租户感知实体**（继承 `TenantEntity` 的实体）？
  - 例：`User` + `ApiKey`、`User` + `Merchant`
  - **如存在 → 必须拆为 N+1 查询，或联系架构负责人讨论**

### 2. MyBatis XML Mapper
- [ ] 是否新增了 `*Mapper.xml` 文件？
  - **如新增 → 必须检查是否有手写 JOIN**
  - 项目所有 SQL 均通过 MyBatis-Plus LambdaQueryWrapper 构建，禁止手写 JOIN

### 3. 报表/分析类查询
- [ ] 查询是否指向 `rpt_*` 或其他 **独立 analytics schema**？
  - **主数据层禁止跨租户聚合查询**

### 4. 实体类变更
- [ ] 新增实体是否显式继承 `TenantEntity`（租户感知）或 `BaseEntity`（平台级）？
  - **禁止继承 `BaseEntity` 但内部手写 `tenantId` 字段**（导致 DB 有列但逻辑无映射）

### 5. SQL 文件变更
- [ ] `sql/migrations/` 目录下的 migration 文件是否经过 DBA review？
- [ ] 新增表是否有明确的平台级/租户级注释？

---

## 背景说明

当前数据权限模型：

| 类型 | 实体 | 拦截行为 |
|------|------|----------|
| 租户感知 | `User`, `ApiKey`, `Merchant` 等 | MyBatis TenantInterceptor 自动加 `WHERE tenant_id = ?` |
| 平台级 | `Menu`, `Role`, `RoleMenu`, `UserRole` | 无租户拦截，所有租户共享 |

**核心约束**：主数据层使用 **N+1 查询模式**，不依赖 JOIN。所有跨表数据在业务层组合。

**报表/分析** 走独立 `rpt_*` schema，通过 ETL 同步，不在主库做跨租户 JOIN。

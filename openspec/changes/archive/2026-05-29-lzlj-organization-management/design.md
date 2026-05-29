## Context

LZLJ 需要机构管理功能来支持树形组织结构（总代理 → 省代 → 市代 → 门店）。当前所有业务表（user、menu、role 等）已有 `orgId` 字段，但缺少机构表和数据隔离机制。

**现状分析**:
- JWT 生成时已包含 `orgId` claim
- `JwtContextFilter` 未解析 `orgId`
- `UserContext` 无 `orgId` ThreadLocal
- 数据库无 `lzlj_auth_org` 表

**目标**: 实现完整的机构管理，支持树形查询和数据隔离。

## Goals / Non-Goals

**Goals:**
- 创建 `lzlj_auth_org` 机构表（树形结构）
- 实现机构 CRUD API
- 实现机构树形查询 API
- 在 `UserContext` 中支持 `orgId`
- `JwtContextFilter` 解析 JWT 中的 `orgId`
- 所有新建业务数据（用户、菜单、角色）必须关联 `orgId`

**Non-Goals:**
- 权限控制细化到机构级别（仅做数据隔离，不做细粒度权限）
- 机构迁移（树的重新parent）
- 机构数据导入导出

## Decisions

### 1. 机构表设计

**选择**: 沿用原有的 Organization 实体设计（稍有调整）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| parent_id | BIGINT | 父机构ID，根节点为0 |
| level_path | VARCHAR(255) | 层级路径 /1/2/3/ |
| level | INT | 层级深度，根节点为1 |
| org_code | VARCHAR(64) | 机构编码 |
| org_name | VARCHAR(128) | 机构名称 |
| org_type | INT | 1:总代理 2:省代 3:市代 4:门店 |
| status | INT | 0:禁用 1:启用 |
| sort | INT | 排序 |

**替代方案考虑**:
- **邻接表（parent_id only）**: 简单但查询子树需要递归，效率低
- **闭包表**: 查询高效但复杂度高，维护成本大
- **最终选择 level_path**: 查询子树只需 `LIKE '/1/2/%'`，平衡了简单性和查询效率

### 2. 机构实体继承

**选择**: 继承 `BaseEntity` 而非 `TenantEntity`

lzlj 没有多租户概念，不应继承 `TenantEntity`。`TenantEntity` 中的 `orgId` 字段对 lzlj 无意义。

### 3. JWT orgId 解析

**选择**: 在 `JwtContextFilter` 中解析 `orgId` 并存入 `UserContext`

```java
Long orgId = claims.get("orgId", Long.class);
if (orgId != null) {
    UserContext.setOrgId(orgId);
}
```

## Risks / Trade-offs

[Risk] 机构删除时子机构处理
→ ** Mitigation**: 删除时检查是否有子机构或关联用户，有则拒绝删除

[Risk] 机构移动（修改 parent_id）
→ ** Mitigation**: 当前暂不支持移动操作，仅支持禁用

## Open Questions

1. 是否需要机构管理员角色（某个机构的管理员只能管理本机构数据）？
2. 超级管理员（user_type=1）是否不受机构限制？

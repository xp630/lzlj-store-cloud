## Why

LZLJ 目前没有机构管理功能，所有业务数据（用户、菜单、角色、日志等）虽然有 `orgId` 字段，但无法进行机构维度的管理和数据隔离。机构是一个树形结构（总代理 → 省代 → 市代 → 门店），需要实现完整的机构管理功能。

同时，JWT 中虽然携带了 `orgId`，但 `UserContext` 未解析和存储 `orgId`，导致服务层无法直接获取当前用户的机构信息。

## What Changes

1. **机构实体与表**: 创建 `LzljOrg` 实体和 `lzlj_auth_org` 表（树形结构）
2. **UserContext 增强**: 添加 `orgId` 支持
3. **JwtContextFilter 增强**: 解析 JWT 中的 `orgId` 并设置到 UserContext
4. **机构 CRUD API**: 创建、查询、更新、删除机构
5. **机构树形 API**: 获取完整树、按父节点获取子节点
6. **数据关联**: 用户、菜单、角色等创建时必须关联机构

## Capabilities

### New Capabilities
- `lzlj-org-management`: 机构管理（树形机构 CRUD、树形查询）
- `lzlj-org-context`: 机构上下文（UserContext 支持、JWT 解析）

### Modified Capabilities
- （无）

## Impact

- **新表**: `lzlj_auth_org`
- **修改文件**:
  - `UserContext.java` - 添加 orgId ThreadLocal
  - `JwtContextFilter.java` - 解析 JWT 中的 orgId
  - `LzljUserServiceImpl.java` - 确认 JWT 生成包含 orgId
- **新文件**:
  - `LzljOrg.java` - 机构实体
  - `LzljOrgDao.java` - 机构 Mapper
  - `LzljOrgService.java` + `LzljOrgServiceImpl.java` - 机构服务
  - `LzljOrgController.java` - 机构控制器
- **数据库**: 需要执行建表 SQL

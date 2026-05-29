## ADDED Requirements

### Requirement: UserContext 支持 orgId
UserContext SHALL 提供 orgId 的 ThreadLocal 存储和获取方法

#### Scenario: 设置和获取 orgId
- **WHEN** 调用 UserContext.setOrgId(123L)
- **THEN** 后续调用 UserContext.getOrgId() 返回 123L

#### Scenario: 清除上下文
- **WHEN** 调用 UserContext.clear()
- **THEN** orgId 被清除，getOrgId() 返回 null

### Requirement: JwtContextFilter 解析 orgId
JwtContextFilter SHALL 从 JWT 解析 orgId 并设置到 UserContext

#### Scenario: JWT 包含 orgId
- **WHEN** Authorization header 包含有效的 Bearer JWT，且 JWT payload 包含 orgId
- **THEN** UserContext.setOrgId() 被调用

#### Scenario: JWT 不包含 orgId
- **WHEN** JWT payload 不包含 orgId
- **THEN** UserContext.orgId 保持 null

### Requirement: orgId 自动传递
所有需要记录 orgId 的业务操作 SHALL 能从 UserContext 获取 orgId

#### Scenario: 服务层获取 orgId
- **WHEN** 在服务方法中调用 UserContext.getOrgId()
- **THEN** 返回当前请求对应的 orgId

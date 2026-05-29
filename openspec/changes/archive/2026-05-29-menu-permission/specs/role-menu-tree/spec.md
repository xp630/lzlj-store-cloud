## ADDED Requirements

### Requirement: 获取角色已授权菜单（树形）
系统 SHALL 返回指定角色的已授权菜单，以树形结构组织

#### Scenario: 获取已授权菜单树
- **WHEN** 请求 GET /role/{id}/menus/tree，角色存在且有授权菜单
- **THEN** 返回树形结构的菜单列表，children 字段嵌套子菜单

#### Scenario: 角色无授权菜单
- **WHEN** 角色存在但未授权任何菜单
- **THEN** 返回空数组 []

#### Scenario: 角色不存在
- **WHEN** 请求 GET /role/{不存在ID}/menus/tree
- **THEN** 返回错误码 DATA_NOT_FOUND

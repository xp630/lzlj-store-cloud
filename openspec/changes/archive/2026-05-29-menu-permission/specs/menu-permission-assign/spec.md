## ADDED Requirements

### Requirement: 获取全部菜单（带授权状态）
系统 SHALL 返回全部可授权菜单，并标注是否已授权给指定角色

#### Scenario: 获取全部菜单并标注授权状态
- **WHEN** 请求 GET /menu/all?roleId=123
- **THEN** 返回完整菜单树，每个菜单包含 checked 字段表示是否已授权

#### Scenario: checked=true 的情况
- **WHEN** 菜单已授权给指定角色
- **THEN** 该菜单的 checked=true

#### Scenario: checked=false 的情况
- **WHEN** 菜单未授权给指定角色
- **THEN** 该菜单的 checked=false

#### Scenario: 不传 roleId 或 roleId 无效
- **WHEN** 请求 GET /menu/all（不传 roleId）或 roleId=0
- **THEN** 所有菜单的 checked=false（视为"未选择任何角色"）

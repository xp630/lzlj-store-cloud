## ADDED Requirements

### Requirement: 获取当前用户可访问菜单
系统 SHALL 根据当前登录用户的角色返回可访问的菜单树

#### Scenario: 用户有单个角色
- **WHEN** 用户拥有角色A，角色A授权了菜单 [1, 2, 3]
- **THEN** 返回以菜单1为根的树形结构

#### Scenario: 用户有多个角色
- **WHEN** 用户拥有角色A（菜单[1,2]）和角色B（菜单[2,3]）
- **THEN** 返回去重后的菜单树 [1,2,3]

#### Scenario: 用户没有任何角色授权
- **WHEN** 用户没有任何角色，或所有角色都未授权菜单
- **THEN** 返回空数组 []

#### Scenario: 用户未登录
- **WHEN** 请求时用户上下文为空
- **THEN** 返回错误码 AUTH_FAILED

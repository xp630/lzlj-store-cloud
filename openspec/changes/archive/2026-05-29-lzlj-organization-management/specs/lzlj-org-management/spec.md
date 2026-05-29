## ADDED Requirements

### Requirement: 机构树形结构
系统 SHALL 支持树形机构结构，包含以下层级：总代理 → 省代 → 市代 → 门店

### Requirement: 创建机构
系统 SHALL 支持创建新机构，包含以下字段：
- orgCode（机构编码，唯一）
- orgName（机构名称）
- orgType（机构类型：1总代理 2省代 3市代 4门店）
- parentId（父机构ID，根节点为0）
- status（状态：0禁用 1启用）
- sort（排序）

创建时，系统 SHALL 自动计算 level 和 level_path。

#### Scenario: 创建根机构
- **WHEN** 创建 parent_id=0 的机构
- **THEN** level=1，level_path="/{id}/"

#### Scenario: 创建子机构
- **WHEN** 在已有机构(id=5, level=2, level_path="/1/5/")下创建子机构
- **THEN** level=3，level_path="/1/5/{newId}/"

### Requirement: 查询机构树
系统 SHALL 支持获取完整机构树或指定节点的子树

#### Scenario: 获取完整机构树
- **WHEN** 请求 GET /org/tree
- **THEN** 返回所有启用的机构，组装为树形结构

#### Scenario: 获取指定机构下的子树
- **WHEN** 请求 GET /org/{id}/children
- **THEN** 返回该机构下所有子机构（递归）

### Requirement: 查询机构详情
系统 SHALL 支持通过ID查询机构详情

#### Scenario: 查询存在的机构
- **WHEN** 请求 GET /org/{id}
- **THEN** 返回机构完整信息

#### Scenario: 查询不存在的机构
- **WHEN** 请求 GET /org/{不存在ID}
- **THEN** 返回错误码 DATA_NOT_FOUND

### Requirement: 更新机构
系统 SHALL 支持更新机构基本信息

#### Scenario: 更新机构名称
- **WHEN** 请求 PUT /org，传入新的 orgName
- **THEN** orgName 已更新，level 和 level_path 不变

### Requirement: 删除机构
系统 SHALL 支持删除机构（软删除），但需检查依赖

#### Scenario: 删除无关联的机构
- **WHEN** 删除一个没有子机构、没有用户的机构
- **THEN** 删除成功

#### Scenario: 删除有子机构的机构
- **WHEN** 删除一个存在子机构的机构
- **THEN** 返回错误：该机构下存在子机构

#### Scenario: 删除有关联用户的机构
- **WHEN** 删除一个存在用户的机构
- **THEN** 返回错误：该机构下存在用户

# LZLJ 功能清单

> 本文档记录 lzlj-cloud-account 项目中 LZLJ 模块已实现的功能点。

**更新时间**: 2026-05-29

---

## 基础信息

| 项目 | 说明 |
|------|------|
| 服务名 | lzlj-auth |
| 服务端口 | 9091 |
| 数据库 | lzlj_cloud |
| 租户隔离 | 不支持（本地部署模式） |

---

## 一、用户管理 (LzljUser)

### 功能说明
LZLJ 本地用户管理，支持用户的 CRUD 操作。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/entity/LzljUser.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/dao/LzljUserDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/service/LzljUserService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/controller/LzljUserController.java`

### 数据库表
- `lzlj_auth_user` - 用户表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/user | 创建用户 |
| PUT | /lzlj/user/{id} | 更新用户 |
| DELETE | /lzlj/user/{id} | 删除用户 |
| GET | /lzlj/user/{id} | 获取用户详情 |
| GET | /lzlj/user/page | 分页查询用户 |
| GET | /lzlj/user/list | 用户列表 |
| GET | /lzlj/user/current | 获取当前用户 |

### 机构关联
- 用户通过 `orgId` 关联机构
- JWT 中携带 `orgId` 信息

---

## 二、菜单管理 (LzljMenu)

### 功能说明
LZLJ 系统菜单管理，支持菜单的 CRUD 和树形结构查询。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/menu/entity/LzljMenu.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/menu/dao/LzljMenuDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/menu/service/LzljMenuService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/menu/controller/LzljMenuController.java`

### 数据库表
- `lzlj_auth_menu` - 菜单表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/menu | 创建菜单 |
| PUT | /lzlj/menu/{id} | 更新菜单 |
| DELETE | /lzlj/menu/{id} | 删除菜单 |
| GET | /lzlj/menu/{id} | 获取菜单详情 |
| GET | /lzlj/menu/tree | 获取菜单树 |
| GET | /lzlj/menu/my | 获取当前用户可访问菜单 |
| GET | /lzlj/menu/all | 获取全部菜单（带角色授权状态） |
| GET | /lzlj/menu/page | 分页查询菜单 |

### 机构关联
- 菜单通过 `orgId` 关联机构

---

## 三、角色管理 (LzljRole)

### 功能说明
LZLJ 系统角色管理，支持角色的 CRUD 和菜单授权。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/entity/LzljRole.java`
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/entity/LzljRoleMenu.java`
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/entity/LzljUserRole.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/dao/LzljRoleDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/service/LzljRoleService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/role/controller/LzljRoleController.java`

### 数据库表
- `lzlj_auth_role` - 角色表
- `lzlj_auth_role_menu` - 角色菜单关联表
- `lzlj_auth_user_role` - 用户角色关联表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/role | 创建角色 |
| PUT | /lzlj/role/{id} | 更新角色 |
| DELETE | /lzlj/role/{id} | 删除角色 |
| GET | /lzlj/role/{id} | 获取角色详情 |
| GET | /lzlj/role/page | 分页查询角色 |
| GET | /lzlj/role/list | 角色列表 |
| GET | /lzlj/role/{id}/menus | 获取角色菜单 |
| GET | /lzlj/role/{id}/menus/tree | 获取角色菜单树 |
| PUT | /lzlj/role/{id}/menus | 分配菜单权限 |

### 机构关联
- 角色通过 `orgId` 关联机构

---

## 四、机构管理 (LzljOrg)

### 功能说明
LZLJ 机构管理，支持树形机构结构（总代理 → 省代 → 市代 → 门店）。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/entity/LzljOrg.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/dao/LzljOrgDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/service/LzljOrgService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/controller/LzljOrgController.java`

### 数据库表
- `lzlj_auth_org` - 机构表（树形结构）

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/org | 创建机构 |
| PUT | /lzlj/org/{id} | 更新机构 |
| DELETE | /lzlj/org/{id} | 删除机构 |
| GET | /lzlj/org/{id} | 获取机构详情 |
| GET | /lzlj/org/tree | 获取机构树 |
| GET | /lzlj/org/children | 获取子机构 |
| GET | /lzlj/org/all | 获取全部机构 |

### 机构树结构
```
总代理
├── 省代1
│   ├── 市代1
│   │   ├── 门店1
│   │   └── 门店2
│   └── 市代2
│       └── 门店3
└── 省代2
    └── ...
```

---

## 五、日志管理 (LzljLog)

### 功能说明
LZLJ API 访问日志和操作日志记录。

### 相关文件

**API访问日志**
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/log/entity/LzljApiLog.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/log/dao/LzljApiLogDao.java`

**操作日志**
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/log/entity/LzljOperationLog.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/log/dao/LzljOperationLogDao.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/log/controller/LzljLogController.java`

### 数据库表
- `lzlj_auth_api_log` - API访问日志表
- `lzlj_auth_operation_log` - 操作日志表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /lzlj/api-log/page | 分页查询API日志 |
| GET | /lzlj/api-log/list | API日志列表 |
| GET | /lzlj/operation-log/page | 分页查询操作日志 |
| GET | /lzlj/operation-log/list | 操作日志列表 |

### 操作日志
- 通过 `@OperationLog` 注解自动记录
- 模块: user, menu, role, org 等

### 机构关联
- 日志通过 `orgId` 关联机构

---

## 六、OSS上传 (Upload)

### 功能说明
阿里云 OSS 文件上传支持，包括直传和预签名 URL 模式。

### 相关文件
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/upload/service/OssUploadService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/upload/controller/UploadController.java`

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /lzlj/upload/presigned-url | 获取预签名上传URL |
| POST | /lzlj/upload/callback | 上传回调 |

### 功能特性
- 预签名 URL 模式（前端直传 OSS）
- 支持 CDN 域名配置
- 文件类型白名单: image/jpeg, image/png, image/gif, image/webp
- 文件大小限制: 10MB

---

## 七、头像上传 (UserAvatar)

### 功能说明
用户头像上传功能。

### 相关文件
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/user/controller/UserAvatarController.java`

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /lzlj/user/avatar/{userId}/url | 获取头像URL |
| PUT | /lzlj/user/avatar | 更新头像 |

---

## 八、系统参数管理 (LzljSystemParameter)

### 功能说明
LZLJ 系统全局参数配置管理，key-value 模式，支持参数类型校验。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/systemparameter/entity/LzljSystemParameter.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/systemparameter/dao/LzljSystemParameterDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/systemparameter/service/LzljSystemParameterService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/systemparameter/controller/LzljSystemParameterController.java`
- SQL: `sql/lzlj_system_parameter.sql`

### 数据库表
- `lzlj_auth_system_parameter` - 系统参数表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/systemparameter | 创建参数 |
| PUT | /lzlj/systemparameter/{id} | 更新参数 |
| DELETE | /lzlj/systemparameter/{id} | 删除参数 |
| GET | /lzlj/systemparameter/{id} | 获取参数详情 |
| GET | /lzlj/systemparameter/key/{key} | 根据key获取参数 |
| GET | /lzlj/systemparameter/page | 分页查询参数 |
| GET | /lzlj/systemparameter/list | 参数列表 |

### 平台级
- 继承 `BaseEntity`（平台级数据，无机构隔离）

---

## 九、数据字典管理 (LzljDataDictionary)

### 功能说明
LZLJ 系统数据字典管理，扁平结构按 type 分组，用于存储可枚举的业务数据。

### 相关文件
- Entity: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/datadictionary/entity/LzljDataDictionary.java`
- DAO: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/datadictionary/dao/LzljDataDictionaryDao.java`
- Service: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/datadictionary/service/LzljDataDictionaryService.java`
- Controller: `cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/datadictionary/controller/LzljDataDictionaryController.java`
- SQL: `sql/lzlj_data_dictionary.sql`

### 数据库表
- `lzlj_auth_data_dictionary` - 数据字典表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /lzlj/datadictionary | 创建字典项 |
| PUT | /lzlj/datadictionary/{id} | 更新字典项 |
| DELETE | /lzlj/datadictionary/{id} | 删除字典项 |
| GET | /lzlj/datadictionary/{id} | 获取字典项详情 |
| GET | /lzlj/datadictionary/page | 分页查询字典项 |
| GET | /lzlj/datadictionary/list | 字典项列表 |
| GET | /lzlj/datadictionary/type/{type} | 根据类型获取字典项 |
| GET | /lzlj/datadictionary/all-group | 获取所有字典类型分组 |

### 平台级
- 继承 `BaseEntity`（平台级数据，无机构隔离）

---

## 数据库表汇总

| 表名 | 说明 | 机构关联 |
|------|------|----------|
| lzlj_auth_user | 用户表 | 是 |
| lzlj_auth_menu | 菜单表 | 是 |
| lzlj_auth_role | 角色表 | 是 |
| lzlj_auth_role_menu | 角色菜单关联表 | 是 |
| lzlj_auth_user_role | 用户角色关联表 | 是 |
| lzlj_auth_org | 机构表 | 是 |
| lzlj_auth_api_log | API访问日志表 | 是 |
| lzlj_auth_operation_log | 操作日志表 | 是 |
| lzlj_auth_system_parameter | 系统参数表 | 否 |
| lzlj_auth_data_dictionary | 数据字典表 | 否 |

---

## 服务端口

| 服务 | 端口 |
|------|------|
| lzlj-auth | 9091 |
| lzlj-gateway | 18081 |

---

## LZLJ 与 SaaS 差异

| 特性 | LZLJ | SaaS |
|------|------|------|
| 租户隔离 | 不支持 | 支持 |
| 机构层级 | 总代理→省代→市代→门店 | 租户内组织结构 |
| 部署模式 | 本地部署 | 多租户 SaaS |
| 用户关联 | orgId | tenantId |
| 数据范围 | 单机构数据 | 租户内数据隔离 |

---

## 变更记录

| 日期 | 提交 | 说明 |
|------|------|------|
| 2026-05-29 | 541a7e8 | 实现菜单权限管理功能 |
| 2026-05-29 | 2684362 | 添加机构管理功能 |
| 2026-05-29 | 5aece94 | 添加JWT上下文过滤器，修复/user/current接口 |
| 2026-05-29 | a31cc6a | 迁移OSS上传功能到lzlj-auth服务 |
| 2026-05-28 | cbba7fd | 添加OSS上传功能 |

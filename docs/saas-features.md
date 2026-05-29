# SaaS 功能清单

> 本文档记录 lzlj-cloud-account 项目中 SaaS 模块已实现的功能点。

**更新时间**: 2026-05-29

---

## 基础信息

| 项目 | 说明 |
|------|------|
| 服务名 | saas-auth |
| 服务端口 | 9092 |
| 数据库 | saas_account |
| 租户隔离 | 支持（TenantEntity 继承链） |

---

## 一、用户管理 (User)

### 功能说明
SaaS 租户用户管理，支持用户的 CRUD 操作。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/entity/User.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/dao/UserDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/service/UserService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/controller/UserController.java`
- SQL: `sql/saas_sys_user.sql`

### 数据库表
- `saas_sys_user` - 用户表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user/register | 用户注册 |
| POST | /user/login | 用户登录 |
| GET | /user/current | 获取当前用户 |
| PUT | /user/{id} | 更新用户 |
| DELETE | /user/{id} | 删除用户 |
| GET | /user/page | 分页查询用户 |
| GET | /user/list | 用户列表 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 二、菜单管理 (Menu)

### 功能说明
系统菜单管理，支持菜单的 CRUD 和树形结构查询。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/menu/entity/Menu.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/menu/dao/MenuDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/menu/service/MenuService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/menu/controller/MenuController.java`
- SQL: `sql/saas_menu.sql`

### 数据库表
- `saas_auth_menu` - 菜单表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /menu | 创建菜单 |
| PUT | /menu/{id} | 更新菜单 |
| DELETE | /menu/{id} | 删除菜单 |
| GET | /menu/{id} | 获取菜单详情 |
| GET | /menu/tree | 获取菜单树 |
| GET | /menu/my | 获取当前用户可访问菜单 |
| GET | /menu/all | 获取全部菜单（带角色授权状态） |
| GET | /menu/page | 分页查询菜单 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 三、角色管理 (Role)

### 功能说明
系统角色管理，支持角色的 CRUD 和菜单授权。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/role/entity/Role.java`
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/role/entity/RoleMenu.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/role/dao/RoleDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/role/service/RoleService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/role/controller/RoleController.java`
- SQL: `sql/saas_role.sql`, `sql/saas_role_menu.sql`

### 数据库表
- `saas_auth_role` - 角色表
- `saas_auth_role_menu` - 角色菜单关联表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /role | 创建角色 |
| PUT | /role/{id} | 更新角色 |
| DELETE | /role/{id} | 删除角色 |
| GET | /role/{id} | 获取角色详情 |
| GET | /role/page | 分页查询角色 |
| GET | /role/list | 角色列表 |
| GET | /role/{id}/menus | 获取角色菜单 |
| GET | /role/{id}/menus/tree | 获取角色菜单树 |
| PUT | /role/{id}/menus | 分配菜单权限 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 四、租户管理 (Tenant)

### 功能说明
SaaS 多租户管理，分为平台管理员租户和普通租户。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/entity/Tenant.java`
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/entity/AdminTenant.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/dao/TenantDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/service/TenantService.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/service/AdminTenantService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/controller/TenantController.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/tenant/controller/AdminTenantController.java`
- SQL: `sql/saas_tenant.sql`, `sql/saas_admin_tenant.sql`

### 数据库表
- `saas_auth_tenant` - 租户表
- `saas_auth_admin_tenant` - 平台管理员租户表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /tenant | 创建租户 |
| PUT | /tenant/{id} | 更新租户 |
| DELETE | /tenant/{id} | 删除租户 |
| GET | /tenant/{id} | 获取租户详情 |
| GET | /tenant/page | 分页查询租户 |
| GET | /tenant/list | 租户列表 |
| GET | /admin-tenant/all | 获取所有平台管理员租户 |

### 租户隔离
- `Tenant` 继承 `TenantEntity`
- `AdminTenant` 继承 `BaseEntity`（平台级数据，无租户隔离）

---

## 五、机构管理 (Organization)

### 功能说明
SaaS 机构管理，支持树形机构结构。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/entity/Organization.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/dao/OrganizationDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/service/OrganizationService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/controller/OrganizationController.java`

### 数据库表
- `saas_auth_organization` - 机构表（树形结构）

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /organization | 创建机构 |
| PUT | /organization/{id} | 更新机构 |
| DELETE | /organization/{id} | 删除机构 |
| GET | /organization/{id} | 获取机构详情 |
| GET | /organization/tree | 获取机构树 |
| GET | /organization/children | 获取子机构 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 六、API密钥管理 (ApiKey)

### 功能说明
第三方 API 访问密钥管理，用于 API 开放平台认证。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/openapi/entity/ApiKey.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/openapi/dao/ApiKeyDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/openapi/service/ApiKeyService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/openapi/controller/ApiKeyController.java`
- SQL: `sql/saas_api_key.sql`

### 数据库表
- `saas_auth_api_key` - API密钥表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api-key | 创建API密钥 |
| PUT | /api-key/{id} | 更新API密钥 |
| DELETE | /api-key/{id} | 删除API密钥 |
| GET | /api-key/{id} | 获取API密钥详情 |
| GET | /api-key/page | 分页查询API密钥 |
| GET | /api-key/list | API密钥列表 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 七、日志管理 (Log)

### 功能说明
API 访问日志和操作日志记录。

### 相关文件

**API访问日志**
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/entity/ApiLog.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/dao/ApiLogDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/service/LogService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/controller/ApiLogController.java`
- SQL: `sql/saas_api_log.sql`

**操作日志**
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/entity/OperationLog.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/log/dao/OperationLogDao.java`
- SQL: `sql/saas_operation_log.sql`

### 数据库表
- `saas_auth_api_log` - API访问日志表
- `saas_auth_operation_log` - 操作日志表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api-log/page | 分页查询API日志 |
| GET | /api-log/list | API日志列表 |

### 操作日志
- 通过 `@OperationLog` 注解自动记录
- 模块: user, menu, role, tenant, api-key, payment-channel 等

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 八、商户管理 (Merchant)

### 功能说明
商户信息管理。

### 相关文件
- Entity: `cloud-account-saas-biz-merchant/src/main/java/com/lzlj/account/merchant/entity/Merchant.java`
- DAO: `cloud-account-saas-biz-merchant/src/main/java/com/lzlj/account/merchant/dao/MerchantDao.java`
- Service: `cloud-account-saas-biz-merchant/src/main/java/com/lzlj/account/merchant/service/MerchantService.java`
- Controller: `cloud-account-saas-biz-merchant/src/main/java/com/lzlj/account/merchant/controller/MerchantController.java`
- SQL: `sql/saas_merchant_merchant.sql`

### 数据库表
- `saas_merchant_merchant` - 商户表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /merchant | 创建商户 |
| PUT | /merchant/{id} | 更新商户 |
| DELETE | /merchant/{id} | 删除商户 |
| GET | /merchant/{id} | 获取商户详情 |
| GET | /merchant/page | 分页查询商户 |
| GET | /merchant/list | 商户列表 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 九、支付通道管理 (PaymentChannel)

### 功能说明
支付通道配置管理，支持支付宝、微信、银联等多种支付渠道。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/paymentchannel/entity/PaymentChannel.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/paymentchannel/dao/PaymentChannelDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/paymentchannel/service/PaymentChannelService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/paymentchannel/controller/PaymentChannelController.java`
- SQL: `sql/saas_payment_channel.sql`

**枚举类**
- `cloud-account-common-core/src/main/java/com/lzlj/account/common/core/enums/PaymentChannelEnum.java`
  - UNIONPAY(银联), NETBANK(网商)
- `cloud-account-common-core/src/main/java/com/lzlj/account/common/core/enums/PaymentMethodEnum.java`
  - WECHAT(微信支付), ALIPAY(支付宝), BANK_CARD(银行卡), QUICK_PASS(云闪付), POS(POS机)

### 数据库表
- `saas_auth_payment_channel` - 支付通道表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /payment-channel | 创建支付通道 |
| PUT | /payment-channel/{id} | 更新支付通道 |
| DELETE | /payment-channel/{id} | 删除支付通道 |
| GET | /payment-channel/{id} | 获取支付通道详情 |
| GET | /payment-channel/page | 分页查询支付通道 |
| GET | /payment-channel/list | 支付通道列表 |

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| channel_code | VARCHAR(50) | 通道编码 (UNIONPAY/NETBANK) |
| channel_name | VARCHAR(50) | 通道名称 (银联/网商) |
| payment_method | VARCHAR(200) | 支付方式 (逗号分隔) |
| cloud_account_fee | DECIMAL(10,4) | 云账户管理费率 |
| upstream_cost_fee | DECIMAL(10,4) | 上游成本费率 |
| total_fee_cost | DECIMAL(10,4) | 总费率成本 |
| per_transaction_limit | DECIMAL(12,2) | 单笔限额 |
| status | TINYINT | 状态 (0:禁用 1:启用) |

### 租户隔离
- 继承 `BaseEntity`（平台级数据，无租户隔离）

---

## 十、OSS上传 (Upload)

### 功能说明
阿里云 OSS 文件上传支持，包括直传和预签名 URL 模式。

### 相关文件
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/upload/service/OssUploadService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/upload/controller/UploadController.java`

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /upload/presigned-url | 获取预签名上传URL |
| POST | /upload/callback | 上传回调 |

### 功能特性
- 预签名 URL 模式（前端直传 OSS）
- 支持 CDN 域名配置
- 文件类型白名单: image/jpeg, image/png, image/gif, image/webp
- 文件大小限制: 10MB

### 租户隔离
- 无租户隔离（N/A）

---

## 十一、商品管理 (Goods)

### 功能说明
商品信息管理（示例模块）。

### 相关文件
- Entity: `cloud-account-saas-biz-goods/src/main/java/com/lzlj/account/goods/entity/Goods.java`
- DAO: `cloud-account-saas-biz-goods/src/main/java/com/lzlj/account/goods/dao/GoodsDao.java`
- Service: `cloud-account-saas-biz-goods/src/main/java/com/lzlj/account/goods/service/GoodsService.java`
- Controller: `cloud-account-saas-biz-goods/src/main/java/com/lzlj/account/goods/controller/GoodsController.java`

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /goods | 创建商品 |
| PUT | /goods/{id} | 更新商品 |
| DELETE | /goods/{id} | 删除商品 |
| GET | /goods/{id} | 获取商品详情 |
| GET | /goods/page | 分页查询商品 |

### 租户隔离
- 继承 `TenantEntity`，支持租户隔离

---

## 十二、头像上传 (UserAvatar)

### 功能说明
用户头像上传功能。

### 相关文件
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/user/controller/UserAvatarController.java`

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /user/avatar/{userId}/url | 获取头像URL |
| PUT | /user/avatar | 更新头像 |

### 租户隔离
- 支持租户隔离

---

## 十三、系统参数管理 (SystemParameter)

### 功能说明
系统全局参数配置管理，key-value 模式，支持参数类型校验。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/systemparameter/entity/SystemParameter.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/systemparameter/dao/SystemParameterDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/systemparameter/service/SystemParameterService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/systemparameter/controller/SystemParameterController.java`
- SQL: `sql/saas_system_parameter.sql`

**枚举类**
- `cloud-account-common-core/src/main/java/com/lzlj/account/common/core/enums/ParamTypeEnum.java`
  - STRING(字符串), INTEGER(整数), BOOLEAN(布尔值), DECIMAL(小数)

### 数据库表
- `saas_auth_system_parameter` - 系统参数表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /system-parameter | 创建参数 |
| PUT | /system-parameter/{id} | 更新参数 |
| DELETE | /system-parameter/{id} | 删除参数 |
| GET | /system-parameter/{id} | 获取参数详情 |
| GET | /system-parameter/key/{key} | 根据key获取参数 |
| GET | /system-parameter/page | 分页查询参数 |
| GET | /system-parameter/list | 参数列表 |

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| param_key | VARCHAR(100) | 参数编码（全局唯一） |
| param_name | VARCHAR(100) | 参数名称 |
| param_value | VARCHAR(500) | 参数值 |
| param_type | VARCHAR(20) | 参数类型 |
| status | TINYINT | 状态（0:禁用 1:启用） |

### 参数类型
- STRING - 字符串
- INTEGER - 整数（校验格式）
- BOOLEAN - 布尔值（true/false）
- DECIMAL - 小数（校验格式）

### 租户隔离
- 继承 `BaseEntity`（平台级数据，无租户隔离）

---

## 十四、数据字典管理 (DataDictionary)

### 功能说明
系统数据字典管理，扁平结构按 type 分组，用于存储可枚举的业务数据。

### 相关文件
- Entity: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/datadictionary/entity/DataDictionary.java`
- DAO: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/datadictionary/dao/DataDictionaryDao.java`
- Service: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/datadictionary/service/DataDictionaryService.java`
- Controller: `cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/datadictionary/controller/DataDictionaryController.java`
- SQL: `sql/saas_data_dictionary.sql`

### 数据库表
- `saas_auth_data_dictionary` - 数据字典表

### API 端点
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /data-dictionary | 创建字典项 |
| PUT | /data-dictionary/{id} | 更新字典项 |
| DELETE | /data-dictionary/{id} | 删除字典项 |
| GET | /data-dictionary/{id} | 获取字典项详情 |
| GET | /data-dictionary/page | 分页查询字典项 |
| GET | /data-dictionary/list | 字典项列表 |
| GET | /data-dictionary/type/{type} | 根据类型获取字典项 |
| GET | /data-dictionary/all-group | 获取所有字典类型分组 |

### 字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| dict_code | VARCHAR(100) | 字典编码（全局唯一） |
| dict_type | VARCHAR(50) | 字典类型（分组） |
| dict_label | VARCHAR(100) | 字典标签（显示名称） |
| dict_value | VARCHAR(100) | 字典值（存储值） |
| sort | INT | 排序 |
| status | TINYINT | 状态（0:禁用 1:启用） |

### 租户隔离
- 继承 `BaseEntity`（平台级数据，无租户隔离）

---

## 已废弃功能

### 支付场景 (PaymentScenario)
- 状态: **已废弃**
- 说明: 原计划用于配置支付场景，现已被支付通道管理取代

---

## 数据库表汇总

| 表名 | 说明 | 租户隔离 |
|------|------|----------|
| saas_sys_user | 用户表 | 是 |
| saas_auth_menu | 菜单表 | 是 |
| saas_auth_role | 角色表 | 是 |
| saas_auth_role_menu | 角色菜单关联表 | 是 |
| saas_auth_tenant | 租户表 | 是 |
| saas_auth_admin_tenant | 平台管理员租户表 | 否 |
| saas_auth_organization | 机构表 | 是 |
| saas_auth_api_key | API密钥表 | 是 |
| saas_auth_api_log | API访问日志表 | 是 |
| saas_auth_operation_log | 操作日志表 | 是 |
| saas_merchant_merchant | 商户表 | 是 |
| saas_auth_payment_channel | 支付通道表 | 否 |
| saas_auth_system_parameter | 系统参数表 | 否 |
| saas_auth_data_dictionary | 数据字典表 | 否 |

---

## 枚举类汇总

| 枚举类 | 路径 | 枚举值 |
|--------|------|--------|
| PaymentChannelEnum | common-core/enums | UNIONPAY(银联), NETBANK(网商) |
| PaymentMethodEnum | common-core/enums | WECHAT, ALIPAY, BANK_CARD, QUICK_PASS, POS |
| ParamTypeEnum | common-core/enums | STRING, INTEGER, BOOLEAN, DECIMAL |

---

## 服务端口

| 服务 | 端口 |
|------|------|
| saas-auth | 9092 |
| saas-gateway | 18080 |
| saas-goods | 9093 |
| saas-merchant | 9094 |

---

## 变更记录

| 日期 | 提交 | 说明 |
|------|------|------|
| 2026-05-29 | f311cfd | 添加支付通道管理模块 |
| 2026-05-29 | 541a7e8 | 实现菜单权限管理功能 |
| 2026-05-29 | 2684362 | 添加机构管理功能 |
| 2026-05-29 | 5aece94 | 添加JWT上下文过滤器 |
| 2026-05-29 | a31cc6a | 迁移OSS上传功能到lzlj-auth服务 |
| 2026-05-29 | cbba7fd | 添加OSS上传功能 |
| 2026-05-28 | 704d5d0 | 新增商户服务模块 |
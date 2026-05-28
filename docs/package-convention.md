# Java 包命名与目录结构规范

## 规范目的

统一项目包命名，避免 `com.lzlj.store`、`com.lzlj.lzlj` 等混用。

## 一、顶级包命名

| 业务线 | 包前缀 | 说明 |
|--------|--------|------|
| 泸州老窖主业务 | `com.lzlj.account` | 所有 account 相关业务 |

**历史遗留**：`com.lzlj.store` 已废弃，禁止新增使用。

## 二、业务模块包结构

```
com.lzlj.account.{模块名}.*
│
├── {模块名}/
│   ├── controller/     # 控制层
│   ├── service/       # 服务接口
│   │   └── impl/      # 服务实现
│   ├── dao/           # 数据访问层 (Mapper)
│   ├── mapper/        # MyBatis Mapper 接口
│   ├── entity/        # 实体类（MyBatis 映射用）
│   ├── dto/           # 数据传输对象（统一使用，不再区分 VO）
│   ├── config/        # 配置类
│   └── handler/      # 处理器 (如 Sentinel BlockHandler)
│
├── {模块名}Application.java  # 启动类
└── log/                # 日志相关（操作日志、API日志）
```

## 三、当前项目包结构

| 模块 | 包名 | 端口 | 说明 |
|------|------|------|------|
| cloud-account-lzlj-gateway | `com.lzlj.account.gateway` | 18080 | 网关服务 |
| cloud-account-lzlj-auth | `com.lzlj.account.user` | 9092 | 用户服务 |
| cloud-account-lzlj-user | `com.lzlj.account.user` | 9093 | LZLJ 用户服务 |
| cloud-account-common-api | `com.lzlj.account.common.api.*` | - | 公共 API 模块 |
| cloud-account-common-core | `com.lzlj.account.common.core.*` | - | 公共核心模块 |

## 四、目录 vs 包名对照

```
src/main/java/
└── com/lzlj/account/{模块名}/
    └── {模块名}/
        ├── controller/
        ├── service/
        │   └── impl/
        ├── dao/
        ├── mapper/
        ├── entity/
        ├── dto/
        ├── config/
        └── handler/
```

**示例**：用户服务
```
src/main/java/
└── com/lzlj/account/user/
    └── user/
        ├── controller/UserController.java  → package com.lzlj.account.user.controller;
        ├── service/UserService.java        → package com.lzlj.account.user.service;
        ├── service/impl/UserServiceImpl.java → package com.lzlj.account.user.service.impl;
        ├── mapper/UserMapper.java          → package com.lzlj.account.user.mapper;
        ├── entity/UserEntity.java          → package com.lzlj.account.user.entity;
        └── dto/UserDTO.java               → package com.lzlj.account.user.dto;
```

## 五、命名规则

### 5.1 类命名

| 类型 | 规则 | 示例 |
|------|------|------|
| Controller | `{业务}Controller` | `UserController` |
| Service | `{业务}Service` | `UserService` |
| Service Impl | `{业务}ServiceImpl` | `UserServiceImpl` |
| DAO/Mapper | `{业务}Dao` 或 `{业务}Mapper` | `UserMapper` |
| Entity | `{业务}Entity` | `UserEntity` |
| DTO | `{业务}DTO` | `UserDTO`, `UserCreateDTO` |
| Config | `{功能}Config` | `RedisConfig` |
| Handler | `{功能}Handler` | `GlobalExceptionHandler` |
| Fallback | `{FeignClient}Fallback` | `UserFeignClientFallback` |

**注**：统一使用 DTO，不再区分 VO。

### 5.2 包名命名

- 全小写
- 不使用中划线、下划线
- 模块名使用有意义的单词

### 5.3 配置类

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 配置属性 | `@ConfigurationProperties(prefix = "xxx")` | - |
| 配置类 | `{功能}Config` | `CorsConfig` |

## 六、禁止模式

❌ **禁止**使用：
- `com.company.module.impl` （impl 多余）
- `com.company.module.server` （server 多余）
- `com.lzlj.store.*` （已废弃）
- `com.lzlj.lzlj.*` （重复顶级包）

✅ **正确**使用：
- `com.lzlj.account.gateway.*`
- `com.lzlj.account.user.*`
- `com.lzlj.account.common.core.*`
- `com.lzlj.account.common.api.*`

## 七、Git 提交规范

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | 修复 bug |
| docs | 文档更新 |
| style | 代码格式（不影响功能） |
| refactor | 重构 |
| test | 测试 |
| chore | 构建/工具 |

## 八、重构检查清单

当进行包重构时，确保：

- [ ] 所有 Java 文件的 `package` 声明已更新
- [ ] 所有 `import` 语句已更新
- [ ] `@SpringBootApplication(scanBasePackages = {...})` 已更新
- [ ] `@ComponentScan(basePackages = {...})` 已更新
- [ ] `@EnableFeignClients(basePackages = {...})` 已更新
- [ ] `@MapperScan("...")` 已更新
- [ ] 目录结构与包名一致
- [ ] 编译通过
- [ ] IDE 已刷新
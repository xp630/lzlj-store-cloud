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
│   ├── entity/        # 实体类
│   ├── dto/           # 数据传输对象
│   ├── vo/            # 视图对象
│   ├── config/        # 配置类
│   └── handler/      # 处理器 (如 Sentinel BlockHandler)
│
├── {模块名}Application.java  # 启动类
```

## 三、当前项目包结构

| 模块 | 包名 | 端口 | 说明 |
|------|------|------|------|
| account-gateway | `com.lzlj.account.gateway` | 18080 | 网关服务 |
| account-saas-auth | `com.lzlj.account.user` | 9092 | SaaS 用户服务 |
| account-saas-goods | `com.lzlj.account.goods` | 9091 | SaaS 商品服务 |
| account-lzlj-user | `com.lzlj.account.user` | 9093 | LZLJ 用户服务 |
| account-common | `com.lzlj.account.common.*` | - | 公共模块 |

## 四、目录 vs 包名对照

```
src/main/java/
└── com/lzlj/account/{模块名}/
    └── {模块名}/
        ├── controller/
        ├── service/
        ├── dao/
        ├── entity/
        ├── dto/
        ├── vo/
        ├── config/
        └── handler/
```

**示例**：商品服务
```
src/main/java/
└── com/lzlj/account/goods/
    └── goods/
        ├── controller/GoodsController.java  → package com.lzlj.account.goods.controller;
        ├── service/GoodsService.java        → package com.lzlj.account.goods.service;
        └── vo/GoodsVO.java                 → package com.lzlj.account.goods.vo;
```

## 五、命名规则

### 5.1 类命名

| 类型 | 规则 | 示例 |
|------|------|------|
| Controller | `{业务}Controller` | `GoodsController` |
| Service | `{业务}Service` | `GoodsService` |
| Service Impl | `{业务}ServiceImpl` | `GoodsServiceImpl` |
| DAO/Mapper | `{业务}Dao` 或 `{业务}Mapper` | `UserDao`, `GoodsMapper` |
| Entity | `{业务}` 或 `{业务}Entity` | `User`, `GoodsEntity` |
| DTO | `{业务}DTO` | `UserDTO`, `OrderDTO` |
| VO | `{业务}VO` | `UserVO`, `GoodsVO` |
| Config | `{功能}Config` | `RedisConfig`, `FeignConfig` |
| Handler | `{功能}Handler` | `GlobalExceptionHandler` |
| Fallback | `{FeignClient}Fallback` | `UserFeignClientFallback` |

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
- `com.lzlj.account.goods.*`
- `com.lzlj.account.user.*`
- `com.lzlj.account.common.*`

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

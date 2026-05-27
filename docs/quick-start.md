# 新人上手指南

## 一、环境准备

### 1.1 必需中间件

| 中间件 | 版本 | 用途 | 默认端口 |
|--------|------|------|----------|
| JDK | 1.8+ | Java 运行时 | - |
| Maven | 3.6+ | 项目构建 | - |
| MySQL | 8.0+ | 主数据库 | 3306 |
| Redis | 6.0+ | 缓存 | 6379 |
| Nacos | 2.3.x | 注册中心 + 配置中心 | 8848 |

### 1.2 开发工具推荐

- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse
- **数据库客户端**: DBeaver / Navicat
- **API 测试**: Postman / Apifox
- **Redis 客户端**: RedisInsight

### 1.3 环境变量配置

```bash
# .bashrc 或 .zshrc
export JAVA_HOME=/path/to/jdk1.8
export MAVEN_HOME=/path/to/maven
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
```

---

## 二、代码导入与构建

### 2.1 克隆代码

```bash
git clone https://github.com/xp630/lzlj-store-cloud.git
cd lzlj-store-cloud
```

### 2.2 Maven 构建

```bash
# 全量构建
mvn clean install -DskipTests

# 仅编译
mvn compile

# 指定模块构建
mvn clean install -pl account-biz-saas/account-biz-saas-auth -am -DskipTests
```

### 2.3 导入 IDE

1. File → Open → 选择项目根目录
2. 选择 Maven 项目导入
3. 等待依赖下载完成（约 5-10 分钟）

---

## 三、配置文件说明

### 3.1 三种名称的区别

| 名称类型 | 示例 | 用途 | 在哪里配置 |
|----------|------|------|-----------|
| **模块名** (artifactId) | `account-biz-saas-auth` | Maven 项目标识 | pom.xml |
| **Nacos 服务名** | `saas-auth` | 服务注册与发现 | application.yml → `spring.application.name` |
| **包名** (package) | `com.lzlj.account.user` | 代码组织 | Java 文件 package 声明 |

**重要**: 模块名 ≠ Nacos 服务名！两者可以不同。

### 3.2 配置文件位置

```
本地配置（优先级低）:
  src/main/resources/application.yml
  src/main/resources/application-dev.yml

Nacos 配置（优先级高）:
  Nacos 控制台 → 配置列表 → saas-auth.yml, saas-goods.yml 等
```

### 3.3 本地配置示例

```yaml
# application.yml (saas-auth 示例)
server:
  port: 9092

spring:
  application:
    name: saas-auth        # ← 这就是 Nacos 服务名！
  config:
    import: optional:nacos:saas-auth.yml   # 导入 Nacos 配置
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848
      username: nacos
      password: nacos
```

---

## 四、服务启动顺序

### 4.1 启动顺序（必须按序启动）

```
1. Nacos (注册中心 + 配置中心)
   ↓
2. MySQL + Redis (基础设施)
   ↓
3. account-gateway (网关，端口 18080)
   ↓
4. account-biz-saas-auth (用户服务，端口 9092) ← 基础服务，其他服务可能依赖
   ↓
5. account-biz-saas-goods (商品服务，端口 9091)
   ↓
6. account-biz-lzlj-user (LZLJ 用户服务，端口 9093)
```

### 4.2 启动检查清单

- [ ] Nacos 已启动 (http://localhost:8848/nacos)
- [ ] MySQL 已启动
- [ ] Redis 已启动
- [ ] Nacos 中已导入配置（shared-configs: common.yml 等）
- [ ] 数据库已初始化（执行 SQL 脚本）

---

## 五、API 文档入口

### 5.1 各服务 Swagger 地址

| 服务 | URL | 说明 |
|------|-----|------|
| Gateway | http://localhost:18080/swagger-ui.html | 统一入口 |
| saas-auth | http://localhost:9092/swagger-ui.html | 用户服务 |
| saas-goods | http://localhost:9091/swagger-ui.html | 商品服务 |
| lzlj-user | http://localhost:9093/swagger-ui.html | LZLJ 用户服务 |

### 5.2 OpenAPI 文档

| 服务 | OpenAPI JSON URL |
|------|-----------------|
| saas-auth | http://localhost:9092/v3/api-docs |
| saas-goods | http://localhost:9091/v3/api-docs |

---

## 六、项目结构速览

### 6.1 目录结构

```
lzlj-store-cloud/
├── account-gateway/           # API 网关 (18080)
├── account-common/             # 公共模块
│   ├── account-common-core/  # 核心代码 (Result, Exception)
│   └── account-common-api/   # Feign 接口定义
├── account-biz-saas/          # SaaS 业务
│   ├── account-biz-saas-auth/    # 用户服务 (9092)
│   └── account-biz-saas-goods/   # 商品服务 (9091)
├── account-biz-lzlj/          # LZLJ 定制业务
│   └── account-biz-lzlj-user/    # LZLJ 用户服务 (9093)
└── docs/                      # 文档
```

### 6.2 包结构（以 saas-auth 为例）

```
com.lzlj.account.user/
└── user/
    ├── SaasAuthApplication.java    # 启动类
    ├── controller/                 # Controller
    ├── service/                    # Service
    │   ├── UserService.java       # 接口
    │   └── impl/                  # 实现
    ├── dao/                       # MyBatis Mapper
    ├── entity/                    # 数据库实体
    ├── dto/                       # 本地 DTO
    ├── vo/                        # 视图对象
    ├── config/                    # 配置类
    └── handler/                   # 处理器
```

---

## 七、常见开发场景

### 7.1 如何新增一个 API

1. **在 Controller 中添加接口**

```java
// GoodsController.java
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @GetMapping("/{id}")
    public Result<GoodsVO> getById(@PathVariable Long id) {
        return goodsService.getById(id);
    }
}
```

2. **在 Service 中实现业务逻辑**

```java
// GoodsService.java (impl)
@Service
public class GoodsService {

    public GoodsVO getById(Long id) {
        GoodsEntity entity = goodsDao.selectById(id);
        return GoodsConverter.toVO(entity);
    }
}
```

### 7.2 如何调用其他服务

1. **在 common-api 中定义 FeignClient**

```java
// account-common-api/feign/UserFeignClient.java
@FeignClient(name = "saas-auth", path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);
}
```

2. **在调用方注入使用**

```java
// GoodsController.java (在 saas-goods 中)
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserFeignClient userFeignClient;  // 调用用户服务

    @GetMapping("/owner/{goodsId}")
    public Result<UserVO> getGoodsOwner(@PathVariable Long goodsId) {
        // 调用远程用户服务
        Result<UserDTO> userResult = userFeignClient.getById(goodsId);
        return Result.success(userResult.getData());
    }
}
```

### 7.3 如何添加数据库表

1. 创建 Entity 类

```java
// GoodsEntity.java
@Data
@TableName("goods")
public class GoodsEntity {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer deleted;
}
```

2. 创建 Mapper

```java
// GoodsDao.java (或 GoodsMapper.java)
@Mapper
public interface GoodsDao extends BaseMapper<GoodsEntity> {
}
```

3. 执行 SQL 脚本（在 Nacos 或本地）

### 7.4 如何配置服务降级

参考文档: `docs/feign-degradation.md`

```java
// Fallback 类实现
@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public Result<UserDTO> getById(Long id) {
        return Result.fail("服务暂时不可用");
    }
}

// 使用 Fallback
@FeignClient(name = "saas-auth", path = "/user", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    // ...
}
```

---

## 八、调试验证

### 8.1 验证服务注册

访问 Nacos 控制台：http://localhost:8848/nacos
→ 服务管理 → 服务列表 → 应看到所有已启动服务

### 8.2 验证网关路由

```bash
# 通过网关访问用户服务
curl http://localhost:18080/user/1

# 直接访问用户服务
curl http://localhost:9092/user/1
```

### 8.3 验证服务间调用

1. 启动 saas-auth 和 saas-goods
2. 调用 saas-goods 的接口，该接口内部调用 saas-auth
3. 查看返回结果

---

## 九、问题排查

### 9.1 服务无法启动

| 症状 | 可能原因 | 解决方案 |
|------|----------|----------|
| 端口占用 | 9092 端口被占用 | `lsof -i:9092` 查找进程 |
| Nacos 连接失败 | Nacos 未启动 | 启动 Nacos |
| 数据库连接失败 | MySQL 未启动或配置错误 | 检查 application.yml |

### 9.2 Feign 调用失败

| 症状 | 可能原因 | 解决方案 |
|------|----------|----------|
| 404 Not Found | 路径不匹配 | 检查 @RequestMapping 和 @GetMapping 路径 |
| 500 Error | 远程服务异常 | 查看远程服务日志 |
| 超时 | 远程服务响应慢 | 增加超时配置 |

### 9.3 Nacos 配置不生效

1. 确认配置已添加到 Nacos
2. 确认 `spring.config.import` 配置正确
3. 确认 namespace 和 group 匹配

---

## 十、参考资料

| 文档 | 说明 |
|------|------|
| `docs/architecture-convention.md` | 架构规范 |
| `docs/package-convention.md` | 包命名规范 |
| `docs/feign-degradation.md` | Feign 降级方案 |

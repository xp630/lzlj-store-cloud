# LZLJ Cloud - 泸州老窖云店系统

基于 **JDK 17** + **Spring Cloud Alibaba 2023** 的云原生微服务架构

## 技术栈

| 层级 | 技术 |
|------|------|
| 基础框架 | JDK 17, Spring Boot 3.2.4 |
| 微服务框架 | Spring Cloud Alibaba 2023.0.1.2, Spring Cloud 2023.0.1 |
| 服务注册/配置 | Alibaba Nacos 2.3.3 |
| 服务治理 | Alibaba MSE (Microservice Engine) |
| 服务调用 | Apache Dubbo 3.2.16, OpenFeign |
| 流量控制 | Alibaba Sentinel 1.8.8 |
| 分布式事务 | Seata 2.0.0 (AT模式) |
| 消息队列 | Apache RocketMQ 2.2.3 |
| 缓存 | Redis Cluster, Redisson 3.27.2 |
| 数据库 | MySQL 8.0, ShardingSphere |
| 搜索引擎 | Elasticsearch 8.12.2 |
| 容器编排 | ACK (Alibaba Cloud Kubernetes) |
| 日志采集 | SLS (Simple Log Service) |
| 监控采集 | ARMS (Application Real-Time Monitoring Service) |
| API文档 | SpringDoc OpenAPI 2.4.0 |

## 项目结构

```
lzlj-cloud/
├── pom.xml                           # 父POM
├── store-common/                     # 公共模块
│   ├── store-common-core/            # 核心公共代码
│   └── store-common-api/             # Feign接口定义
├── store-gateway/                    # API网关 (18080)
├── store-user/                       # 用户服务 (9092)
├── store-goods/                      # 商品服务 (9093)
└── sql/                              # SQL脚本
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Gateway | 18080 | API网关统一入口 |
| User | 9092 | 用户服务 |
| Goods | 9093 | 商品服务 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.3.3+
- RocketMQ 4.9+
- Elasticsearch 8.x
- Kubernetes (可选)

### 1. 初始化数据库

```bash
mysql -u root -p < sql/lzlj_cloud.sql
```

### 2. 启动 Nacos

```bash
# 下载并启动 Nacos
tar -xzf nacos-server-2.3.3.tar.gz
cd nacos/bin
./startup.sh -m standalone
```

### 3. 编译项目

```bash
mvn clean install -DskipTests
```

### 4. 启动服务

```bash
# 启动网关
java -jar lzlj-gateway/target/lzlj-gateway.jar

# 启动用户服务
java -jar lzlj-user/target/lzlj-user.jar

# ... 其他服务类似
```

### 5. 访问

- 网关地址: http://localhost:18080
- Swagger文档: http://localhost:18080/swagger-ui.html
- Nacos控制台: http://localhost:8848/nacos (nacos/nacos)

## 架构特性

### 1. 百万并发支撑

- **接入层**: CDN + WAF + ALB/SLB + Spring Cloud Gateway
- **网关层**: 统一鉴权、限流、熔断
- **服务层**: 无状态设计，K8s HPA自动扩缩容
- **数据层**: 分库分表 + 读写分离

### 2. 多级缓存

```
请求 → L1 Caffeine(本地) → L2 Redis(分布式) → L3 MySQL
```

### 3. 弹性伸缩

- 基于K8s + HPA实现秒级扩容
- 30秒内完成扩容响应
- ESS自动伸缩策略

### 4. 分布式事务

- Seata AT模式
- 自动补偿事务
- 最终一致性

### 5. 安全防护

- JWT Token认证
- RBAC权限控制
- 多因素认证
- 全站HTTPS
- DDoS/WAF防护

## API文档

启动服务后访问Swagger UI:

```
http://localhost:18080/swagger-ui.html
```

## 监控

- ARMS 应用监控
- SLS 日志分析
- MSE 服务治理

## License

Apache 2.0

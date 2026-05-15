# 泸州老窖云店系统 — 阿里云迁移技术改造文档

> **目标环境**: 阿里云容器服务 ACK | 服务治理 MSE | 日志采集 SLS | 监控采集 ARMS
> **文档版本**: v1.0
> **更新日期**: 2026-05-15

---

## 一、现状分析

### 1.1 现有技术栈

| 层级 | 现状技术 |
|------|---------|
| 容器编排 | Docker (本地) |
| 服务治理 | Sentinel (standalone) |
| 注册/配置中心 | Nacos (本地 standalone) |
| 数据库 | MySQL 8.0 (本地) |
| 缓存 | Redis (本地) |
| 消息队列 | RocketMQ (本地) |
| 分布式事务 | Seata (本地) |
| 日志 | 本地 logback |
| 监控 | Prometheus + Grafana (可选) |

### 1.2 阿里云目标架构

```
                        ┌─────────────┐
                        │   ALB/WAF   │
                        └──────┬──────┘
                               │
                    ┌──────────▼──────────┐
                    │   ACK 容器集群       │
                    │  ┌───────────────┐  │
                    │  │ store-gateway │  │
                    │  │ store-user    │  │
                    │  │ store-goods   │  │
                    │  └───────────────┘  │
                    └──────────┬──────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
   ┌────▼────┐          ┌─────▼─────┐         ┌─────▼─────┐
   │ MSE     │          │  ARMS     │         │   SLS     │
   │ 服务治理 │          │  监控采集  │         │  日志采集  │
   └─────────┘          └───────────┘         └───────────┘
```

---

## 二、配置级别改造

### 2.1 环境变量清单

| 变量名 | 本地默认值 | 阿里云值来源 | 说明 |
|--------|-----------|-------------|------|
| `NACOS_SERVER` | `127.0.0.1:8848` | MSE 托管 Nacos | 命名空间 ID + 内网地址 |
| `DB_HOST` | `localhost` | RDS 内网地址 | `rm-xxxxxx.mysql.rds.aliyuncs.com` |
| `DB_PORT` | `3306` | RDS 端口 | 通常 `3306` |
| `DB_NAME` | `lzlj_xxx` | RDS 数据库名 | 按服务拆分 |
| `DB_USER` | `root` | RDS 账号 | 从密钥管理获取 |
| `DB_PASSWORD` | `password123` | RDS 密码 | 从密钥管理获取 |
| `REDIS_HOST` | `localhost` | 云 Redis 内网地址 | `redis-xxxxx.redis.rds.aliyuncs.com` |
| `REDIS_PORT` | `6379` | 云 Redis 端口 | 通常 `6379` |
| `REDIS_PASSWORD` | - | 云 Redis 密码 | 从密钥管理获取 |
| `ROCKETMQ_NAMESRV` | `localhost:9876` | 阿里云 RocketMQ | MQ 控制台获取内网端点 |
| `MSE_ENDPOINT` | - | MSE 内网地址 | MSE 控制台获取 |
| `ARMS_ENDPOINT` | - | ARMS 内网地址 | ARMS 控制台获取 |
| `SLS_ENDPOINT` | - | SLS 内网地址 | SLS 控制台获取 |
| `SLS_PROJECT` | - | SLS 项目名 | SLS 控制台获取 |
| `ROLE_ARN` | - | RAM Role ARN | ACK Pod 挂载 RAM 角色 |

### 2.2 Nacos 配置改造 (`config/nacos/prod/common.yml`)

**改动项**:

```yaml
# 改动前
dubbo:
  registry:
    address: nacos://${NACOS_SERVER:127.0.0.1:8848}
    parameters:
      namespace: ${NACOS_NAMESPACE:prod}
      group: ${NACOS_GROUP:LZLJ_GROUP}

# 改动后 (MSE 托管 Nacos)
dubbo:
  registry:
    address: nacos://${NACOS_SERVER}   # MSE Nacos 内网地址
    parameters:
      namespace: ${NACOS_NAMESPACE}     # MSE 命名空间 ID
      group: ${NACOS_GROUP:LZLJ_GROUP}
```

**Seata 配置改动**:

```yaml
# 改动前
seata:
  enabled: true
  service:
    grouplist:
      default: 127.0.0.1:8091
  config:
    type: nacos
    nacos:
      server-addr: ${NACOS_SERVER:127.0.0.1:8848}

# 改动后 (MSE Seata)
seata:
  enabled: true
  mse.enabled: true                     # 接入 MSE Seata
  tx-service-group: ${NACOS_GROUP:LZLJ_GROUP}-tx-group
  service:
    vgroup-mapping:
      ${NACOS_GROUP:LZLJ_GROUP}-tx-group: default
    grouplist:
      default: ${MSE_SEATA_ENDPOINT}  # MSE Seata 内网地址
```

### 2.3 ARMS 监控接入

**改动文件**: `store-*/src/main/resources/application.yml`

```yaml
# 新增 ARMS 配置
spring:
  cloud:
    arms:
      enabled: true
      runtime:
        endpoint: ${ARMS_ENDPOINT}
      appId: ${ARMS_APP_ID}
      regionId: cn-chengdu
```

**ACK Pod 挂载 ARMS Agent** (通过 annotations 自动注入):

```yaml
# ack-pod-annotations
msePilotAutoEnable: "true"
msePilotCreateAppName: "store-goods"   # 按实际服务名修改
```

### 2.4 SLS 日志采集接入

**第一步**: 添加 Maven 依赖 (`pom.xml`):

```xml
<!-- 在 store-*/pom.xml 中添加 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>aliyun-log-logback-appender</artifactId>
    <version>0.1.4</version>
</dependency>
```

**第二步**: 改造 `logback-spring.xml`:

```xml
<!-- 改动前 -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>

<!-- 改动后 -->
<appender name="SLS" class="com.aliyun.openservices.logback.LoghubAppender">
    <endpoint>${SLS_ENDPOINT}</endpoint>
    <project>${SLS_PROJECT}</project>
    <logstore>${spring.application.name}</logstore>
    <topic>${DEPLOY_ENV:prod}</topic>
    <source>pod-${HOSTNAME}</source>
    <accessKeyId>${ALIBABA_CLOUD_ACCESS_KEY_ID}</accessKeyId>
    <accessKeySecret>${ALIBABA_CLOUD_ACCESS_KEY_SECRET}</accessKeySecret>
    <timeFormat>yyyy-MM-dd'T'HH:mm:ss.SSSZ</timeFormat>
    <timeZone>Asia/Shanghai</timeZone>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

---

## 三、代码级别改造

### 3.1 ACK 就绪/存活探针

**改动文件**: `store-*/src/main/resources/application.yml`

```yaml
# 改动后 — 添加 K8s 探针配置
server:
  port: 18080
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
```

### 3.2 Pod 身份 — STS Token 替代静态密钥

**说明**: ACK 中通过 `alibaba-cloud-credentials` 挂载 RAM Role，避免硬编码 AK/SK。

所有需要访问云资源的 Pod 添加 annotation:

```yaml
metadata:
  annotations:
    mse.alibabacloud.com/credentials.type: "sts"
    mse.alibabacloud.com/credentials.role-arn: "${ROLE_ARN}"
```

### 3.3 Dubbo 云上改造 — MSE 服务治理

**改动文件**: `config/nacos/prod/common.yml`

```yaml
dubbo:
  registry:
    # 改动前: nacos://${NACOS_SERVER}
    # 改动后: mse://${MSE_DUBBO_ENDPOINT}
    address: mse://${MSE_DUBBO_ENDPOINT}
    parameters:
      namespace: ${MSE_NAMESPACE}
      group: ${MSE_GROUP}
  provider:
    timeout: 3000
    retries: 1
  consumer:
    timeout: 5000
    check: false
```

### 3.4 数据库连接 — Druid + 云 RDS

**改动文件**: `store-*/src/main/resources/application-prod.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://${RDS_INTERNAL_HOST}:${RDS_PORT}/${DB_NAME}?useSSL=true&serverTimezone=Asia/Shanghai&requireSSL=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${RDS_USER}
    password: ${RDS_PASSWORD}
    druid:
      # 建议开启 SSL 校验
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000;config.decrypt=false
```

### 3.5 Seata 与 ShardingSphere 冲突处理

**问题**: ShardingSphere 与 Seata 存在连接池冲突，云上推荐使用 **MSE Seata**。

**方案**: 应用侧仅保留 `seata-spring-boot-starter` 依赖，由 MSE 托管 TC 集群，应用不做 Seata DataSource 代理:

```yaml
# store-goods/application.yml — 排除 Seata DataSource 自动代理
spring:
  autoconfigure:
    exclude:
      - io.seata.spring.boot.autoconfigure.SeataAutoConfiguration
      - io.seata.spring.boot.autoconfigure.SeataDataSourceAutoConfiguration
      # 注意: 保留 SeataAutoConfiguration，仅禁用 DataSource 代理
```

---

## 四、ACK 部署文件

### 4.1 目录结构

```
deployments/
├── service-account.yml        # RAM Role + ServiceAccount
├── configmap.yml              # 非敏感配置
├── secret.yml                 # 加密凭据 (KMS)
├── store-gateway.yml          # Deployment + Service + HPA
├── store-user.yml            # Deployment + Service + HPA
├── store-goods.yml           # Deployment + Service + HPA
└── ingress.yml                # ALB Ingress
```

### 4.2 ServiceAccount + RAM Role (`service-account.yml`)

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: store-app
  namespace: lzlj-cloud
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: store-app-role
  namespace: lzlj-cloud
rules:
  - apiGroups: [""]
    resources: ["configmaps"]
    verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: store-app-role-binding
  namespace: lzlj-cloud
subjects:
  - kind: ServiceAccount
    name: store-app
    namespace: lzlj-cloud
roleRef:
  kind: Role
  name: store-app-role
  apiGroup: rbac.authorization.k8s.io
```

### 4.3 Gateway Deployment + HPA (`store-gateway.yml`)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: store-gateway
  namespace: lzlj-cloud
  labels:
    app: store-gateway
spec:
  replicas: 2
  selector:
    matchLabels:
      app: store-gateway
  template:
    metadata:
      labels:
        app: store-gateway
      annotations:
        mse.alibabacloud.com/credentials.type: "sts"
        mse.alibabacloud.com/credentials.role-arn: "${ROLE_ARN}"
        msePilotAutoEnable: "true"
        msePilotCreateAppName: "store-gateway"
    spec:
      serviceAccountName: store-app
      containers:
        - name: store-gateway
          image: registry.cn-chengdu.aliyuncs.com/lzlj/store-gateway:1.0.0
          ports:
            - containerPort: 18080
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1000m"
              memory: "1Gi"
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 18080
            initialDelaySeconds: 10
            periodSeconds: 5
            failureThreshold: 3
          livenessProbe:
            httpGet:
              path: /actuator/health
              port: 18080
            initialDelaySeconds: 30
            periodSeconds: 10
            failureThreshold: 5
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: NACOS_SERVER
              value: "${NACOS_SERVER}"
            - name: NACOS_NAMESPACE
              value: "${NACOS_NAMESPACE}"
            - name: SPRING_CLOUD_NACOS_USERNAME
              valueFrom:
                secretKeyRef:
                  name: store-secret
                  key: nacos-username
            - name: SPRING_CLOUD_NACOS_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: store-secret
                  key: nacos-password
          lifecycle:
            preStop:
              exec:
                command: ["sh", "-c", "sleep 10"]
---
apiVersion: v1
kind: Service
metadata:
  name: store-gateway-svc
  namespace: lzlj-cloud
spec:
  type: ClusterIP
  ports:
    - port: 80
      targetPort: 18080
  selector:
    app: store-gateway
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: store-gateway-hpa
  namespace: lzlj-cloud
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: store-gateway
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
```

### 4.4 Secret (KMS 加密) (`secret.yml`)

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: store-secret
  namespace: lzlj-cloud
  annotations:
    kafka.kubernetes.io/credentials: "true"
type: Opaque
stringData:
  nacos-username: "${NACOS_USERNAME}"
  nacos-password: "${NACOS_PASSWORD}"
  rds-password: "${RDS_PASSWORD}"
  redis-password: "${REDIS_PASSWORD}"
```

### 4.5 ALB Ingress (`ingress.yml`)

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: store-ingress
  namespace: lzlj-cloud
  annotations:
    kubernetes.io/ingress.class: alb
    alb.aliyun.com/health-check: "true"
spec:
  rules:
    - host: api.lzlj.com
      http:
        paths:
          - path: /
            backend:
              service:
                name: store-gateway-svc
                port:
                  number: 80
            pathType: Prefix
```

---

## 五、改造优先级

| 优先级 | 改造项 | 类型 | 风险 |
|--------|--------|------|------|
| P0 | RDS / Redis / Nacos 地址切换 | 配置 | 低 |
| P0 | ARMS Agent 挂载 | 配置 | 低 |
| P0 | ACK Deployment + HPA | 部署 | 中 — 探针调优 |
| P0 | SLS 日志采集 | 配置 | 低 |
| P1 | MSE 服务治理 (限流/熔断) | 配置 | 中 — 规则迁移 |
| P1 | MSE Seata 分布式事务 | 配置 | 高 — 与 ShardingSphere 冲突 |
| P1 | MSE Dubbo 服务治理 | 配置 | 中 |
| P2 | RocketMQ 云端版 | 配置 | 中 — 客户端版本升级 |
| P2 | ACK Ingress ALB 配置 | 部署 | 低 |

---

## 六、改造检查清单

### 配置项

- [ ] `NACOS_SERVER` 环境变量指向 MSE Nacos
- [ ] `DB_HOST/DB_PORT/DB_NAME` 指向 RDS
- [ ] `REDIS_HOST/REDIS_PORT/PASSWORD` 指向云 Redis
- [ ] `ARMS_ENDPOINT` 配置正确
- [ ] `SLS_ENDPOINT/SLS_PROJECT` 配置正确
- [ ] `spring.profiles.active=prod` 在 Pod 中生效
- [ ] ACK Pod annotation 挂载 MSE Role ARN
- [ ] SLS LoghubAppender 配置正确

### 代码项

- [ ] `management.endpoint.health.show-details=always`
- [ ] K8s `readinessProbe` / `livenessProbe` 配置
- [ ] `server.shutdown=graceful` + `spring.lifecycle.timeout-per-shutdown-phase=30s`
- [ ] Dubbo registry 切换至 `mse://`
- [ ] Seata DataSource 代理排除 (仅 ShardingSphere 共存时)

### 部署项

- [ ] `ServiceAccount` + RAM Role 创建
- [ ] KMS Secret 注入
- [ ] `HorizontalPodAutoscaler` CPU/Memory 阈值调优
- [ ] ALB Ingress 域名解析
- [ ] 灰度发布策略 (金丝雀 10% → 50% → 100%)

### 验证项

- [ ] 所有 Pod `kubectl get pods` status = Running
- [ ] `kubectl logs` 无启动报错
- [ ] ARMS 控制台可见服务调用链路
- [ ] SLS 控制台可搜索到日志
- [ ] MSE 控制台限流规则生效
- [ ] `/actuator/health` 返回 `{"status":"UP"}`

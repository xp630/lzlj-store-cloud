## Why

当前 `cloud-account-lzlj-biz/cloud-account-lzlj-user` 服务端口配置为 9092，与 `cloud-account-saas-biz/cloud-account-saas-biz-auth` (saas-auth) 的端口冲突。两个服务无法同时本地启动，阻碍开发调试。同时项目文档（README.md、ROADMAP.md）仍描述旧 `store-*` 结构，与当前 `cloud-account-*` 实际架构严重脱节，新人上手易产生困惑。

## What Changes

1. **修复端口冲突**: 将 `account-lzlj-user` 服务端口从 9092 调整为 9093；将 `lzlj-auth` 从 9094 调整为 9294；将 `account-gateway-lzlj` 从 18081 调整为 28080
2. **更新 README.md**: 替换为当前 `cloud-account-*` 模块结构和真实端口配置
3. **更新 ROADMAP.md**: 替换旧 `store-*` 描述为当前架构
4. **同步更新 architecture-convention.md 中的过时端口信息**（如有）

## Capabilities

### New Capabilities
- `port-assignment`: 定义项目中各服务的端口分配规范，避免未来冲突

### Modified Capabilities
- 无（本次为修复性改动，不涉及需求变更）

## Impact

### 受影响代码
- `cloud-account-lzlj/cloud-account-lzlj-biz/cloud-account-lzlj-user/src/main/resources/application-dev.yml` — 端口配置
- `cloud-account-lzlj/cloud-account-lzlj-biz/cloud-account-lzlj-user/src/main/resources/application-prod.yml` — 端口配置
- `cloud-account-lzlj/cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/resources/application-dev.yml` — 端口配置 9094→9294
- `cloud-account-lzlj/cloud-account-lzlj-entrance/cloud-account-lzlj-gateway/src/main/resources/application.yml` — 端口配置 18081→28080
- `cloud-account-lzlj/cloud-account-lzlj-entrance/cloud-account-lzlj-gateway/src/main/resources/application-dev.yml` — 端口配置 18081→28080
- `cloud-account-lzlj/cloud-account-lzlj-entrance/cloud-account-lzlj-gateway/src/main/resources/application-prod.yml` — 端口配置 18081→28080

### 受影响文档
- `README.md`
- `ROADMAP.md`
- `docs/architecture-convention.md`（端口信息部分）

### 无影响范围
- `cloud-account-saas` 下所有服务端口保持不变
- `account-lzlj-user` (9093) 端口已在上次修复中调整完毕
- 数据库表结构无变化
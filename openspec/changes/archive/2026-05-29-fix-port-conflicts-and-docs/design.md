## Context

当前项目中存在端口冲突：

| 服务 | 当前端口 | 注册到 Nacos 的服务名 |
|------|---------|---------------------|
| `saas-auth` (cloud-account-saas-biz-auth) | 9092 | `saas-auth` |
| `account-lzlj-user` (cloud-account-lzlj-biz-user) | 9092 | `account-lzlj-user` |

两个服务在本地无法同时启动，违背了项目文档中描述的启动顺序（要求 lzlj-auth 先于 lzlj-user 启动）。

同时 `README.md` 和 `ROADMAP.md` 描述的是旧 `store-*` 架构，与当前 `cloud-account-*` 结构严重脱节。

## Goals / Non-Goals

**Goals:**
- 解决 `account-lzlj-user` 与 `saas-auth` 端口冲突（已完成）
- 将 `lzlj-auth` 端口从 9094 调整为 9294
- 将 `account-gateway-lzlj` 端口从 18081 调整为 28080
- 更新所有文档反映最新端口配置

**Non-Goals:**
- 不修改 `cloud-account-saas` 下任何服务的端口
- 不改变服务间调用关系和 Feign 接口
- 不修改数据库结构
- 不做架构层面的重构

## Decisions

### Decision 1: `account-lzlj-user` 端口改为 9093

**选择**: 将 `account-lzlj-user` 从 9092 改为 9093

**理由**:
- 9093 在文档中被描述为 `lzlj-user` 的预留端口，与实际使用一致
- 9092 保留给 `saas-auth`，保持 SaaS 侧不变
- 9094 已被 `lzlj-auth` 占用
- 9095+ 未被任何服务使用，但 9093 更符合原文档意图

**替代方案**:
- 改为 9095+: 可行，但不如 9093 符合原文档规划

### Decision 2: `lzlj-auth` 端口改为 9294

**选择**: 将 `lzlj-auth` 从 9094 改为 9294

**理由**:
- 929x 是项目预留的 LZLJ 服务端口段
- 28080 是项目预留的 LZLJ 网关端口段
- 与 909x (SaaS) 端口段清晰区分

### Decision 3: `account-gateway-lzlj` 端口改为 28080

**选择**: 将 `account-gateway-lzlj` 从 18081 改为 28080

**理由**:
- 28080 与 18080 (SaaS 网关) 端口段区分清晰
- 便于本地同时启动两套网关进行测试

### Decision 4: 文档完全重写

**选择**: 用当前 `cloud-account-*` 结构和真实端口配置重写 README.md 和 ROADMAP.md

**理由**: 旧文档描述的 `store-*` 模块名、目录结构、端口配置均已过时，简单补丁式修改不足以解决新人上手困惑。

## Risks / Trade-offs

- [Risk] 本地已启动 9092 端口的服务被新配置覆盖 → [Mitigation] 告知开发者需要重启服务，或先用 `lsof -i:9092` 确认端口占用情况
- [Risk] 文档更新后仍有遗漏 → [Mitigation] 更新完成后进行全面检查，确保 README/ROADMAP/architecture-convention 三处一致

## Migration Plan

1. 修改 `account-lzlj-user` 的 `application-dev.yml` 端口: 9092 → 9093（已完成）
2. 修改 `account-lzlj-user` 的 `application-prod.yml` 端口: 9092 → 9093（已完成）
3. 修改 `lzlj-auth` 的 `application-dev.yml` 端口: 9094 → 9294
4. 修改 `account-gateway-lzlj` 的所有配置文件端口: 18081 → 28080
5. 更新所有文档中的端口描述
6. 提交代码，告知团队端口变更

**回滚**: 将各 `application*.yml` 端口分别改回原值即可。
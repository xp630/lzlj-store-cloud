## Why

系统需要支持文件上传能力（SaaS 多租户版用户头像上传为首发场景）。采用前端直传 OSS 的方案（预签名 URL），减少服务端带宽压力。

当前系统已引入阿里云 OSS SDK（`aliyun-sdk-oss`），但未提供统一的上传接口。上传能力分散在各业务中，缺乏统一管理。

## What Changes

1. **新增 OSS 预签名 URL 生成工具类** 放在 `cloud-account-common` 模块
2. **新增文件上传 Controller** 提供 `/upload/presigned-url` 接口
3. **支持可选接入** — Nacos 配置 `oss.enabled=true` 时启用，不配置则服务正常启动但上传功能不可用
4. **用户头像上传 API** — 基于通用上传能力，提供头像专用接口

## Capabilities

### New Capabilities
- `oss-upload`: 阿里云 OSS 前端直传支持，预签名 URL 生成
- `user-avatar`: 用户头像上传，基于 oss-upload 能力

### Modified Capabilities
- 无

## Impact

### 新增代码
- `cloud-account-common/cloud-account-common-oss/` — OSS 工具模块
- `cloud-account-saas/cloud-account-saas-biz/cloud-account-saas-biz-auth/` — 头像上传 API

### 受影响模块
- `cloud-account-common` — 新增可选模块，依赖 aliyun-sdk-oss

### 配置依赖（Nacos）
- `oss.enabled` — 是否启用 OSS（默认 false）
- `oss.endpoint` — OSS endpoint
- `oss.bucket` — bucket 名称
- `oss.access-key-id` — AK
- `oss.access-key-secret` — SK
- `oss.cdn-domain` — CDN 域名（可选，用于返回公网 URL）

### 无影响范围
- 未配置 OSS 时系统正常启动，上传接口返回错误码
- `cloud-account-lzlj` 暂不接入（可复用同一模块）

## Context

前端上传大文件（如头像图片）时，传统的"前端 → 后端 → OSS"链路有以下问题：
- 服务端带宽压力
- 内存占用
- 处理延迟

采用 **OSS 预签名 URL** 方案，前端直接从浏览器上传到 OSS，后端只负责生成签名和记录文件路径。

## Goals / Non-Goals

**Goals:**
- 提供通用 OSS 上传能力，支持前端直传
- 可选接入：配置了 OSS 才启用，不配置系统正常启动
- 统一路径规则：`avatar/{userId}/{uuid}.{ext}`
- 提供用户头像上传 API

**Non-Goals:**
- 不实现后端上传（前端直传场景不需要）
- 不实现多 OSS 切换（单 bucket）
- 不实现图片压缩/裁剪（未来可扩展）

## Decisions

### Decision 1: 新建 `cloud-account-common-oss` 模块

**选择**: 在 `cloud-account-common` 下新建 `cloud-account-common-oss` 子模块

**理由**:
- 保持 common 内部的子模块隔离原则
- 与 `common-redis`、`common-database` 同级
- 可被 SaaS 和 LZLJ 业务模块共同依赖

### Decision 2: @ConditionalOnProperty 可选加载

**选择**: `OssUploadService` 使用 `@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")` 加载

**理由**:
- 未配置时 Bean 不创建，服务正常启动
- 配合门面类 `OssUploadFacade`，调用方无需判断 null
- 不配置 OSS 时调用上传接口返回 `Result.fail("OSS未配置")`

**Nacos 配置项**:
```yaml
oss:
  enabled: true              # 默认 false（不创建 Bean）
  endpoint: oss-cn-chengdu.aliyuncs.com
  bucket: lzlj-cloud-account
  access-key-id: ${OSS_AK}
  access-key-secret: ${OSS_SK}
  cdn-domain: https://cdn.xxx.com  # 可选
```

### Decision 3: 预签名 URL 生成接口

**选择**: 提供 `GET /upload/presigned-url` 接口

**请求**:
```
GET /upload/presigned-url?filename=avatar.jpg&contentType=image/jpeg&size=102400
Header: X-Tenant-Id, X-User-Id
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "uploadUrl": "https://lzlj-cloud-account.oss-cn-chengdu.aliyuncs.com/avatar/1/uuid.jpg?签名...",
    "fileUrl": "https://cdn.xxx.com/avatar/1/uuid.jpg",
    "expireSeconds": 300
  }
}
```

**理由**:
- 前端先调此接口获取上传地址和签名
- 再用 PUT 请求直接上传到 OSS
- 上传完成后前端通知后端记录文件路径

### Decision 4: 文件路径规则

**选择**: `{type}/{userId}/{uuid}.{ext}`

**示例**:
- 头像: `avatar/123/550e8400-e29b.jpg`
- 通用: `file/123/550e8400-e29b.pdf`

**理由**:
- `type` 区分用途（avatar/file/goods）
- `userId` 隔离不同用户文件
- `uuid` 避免文件名冲突
- 不暴露原始文件名（安全）

### Decision 5: 内容校验

**选择**: 后端只校验 `content-type` 白名单 + `size` 上限

**理由**:
- 预签名 URL 阶段只生成签名，不实际处理文件
- 真正的文件校验由 OSS 服务端完成（SDK 校验）
- 前端需要在上传前做格式校验和大小检查

**白名单**: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
**大小限制**: `maxSize` 参数传入，后端校验是否超限（参考值：5MB）

### Decision 6: 门面模式封装

**选择**: `OssUploadFacade` 始终注入，底层 `OssUploadService` 可选

```java
@Component
public class OssUploadFacade {
    private final Optional<OssUploadService> service;

    public Result<PresignedUrlVO> generateUploadUrl(...) {
        return service
            .map(s -> s.generateUploadUrl(...))
            .orElse(Result.fail("OSS未配置"));
    }
}
```

## Risks / Trade-offs

- [Risk] OSS SDK 依赖较重 → [Mitigation] 仅在 `oss.enabled=true` 时加载，default 启动不受影响
- [Risk] 预签名 URL 有效期被猜到 → [Mitigation] URL 加入随机 UUID，签名有时效性（5分钟）
- [Risk] 路径规则变更 → [Mitigation] 路径由后端生成，前端只传 originalFilename

## Migration Plan

1. 新建 `cloud-account-common-oss` 模块目录结构
2. 实现 `OssProperties` 配置绑定类
3. 实现 `OssUploadService` 预签名 URL 生成
4. 实现 `OssUploadFacade` 门面类
5. 实现 `UploadController` 提供 API
6. 实现用户头像上传 API (`/user/avatar/upload`)
7. 在 `cloud-account-saas-biz-auth` 的 `pom.xml` 中引入依赖
8. 在 `application.yml` 中添加 OSS 配置（Nacos 或本地）

**回滚**: 移除 `oss.enabled=true` 配置，或删除模块依赖即可。

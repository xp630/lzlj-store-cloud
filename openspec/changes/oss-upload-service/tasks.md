## 1. Module Setup

- [x] 1.1 创建 `cloud-account-common/cloud-account-common-oss/` 目录结构
- [x] 1.2 创建 `cloud-account-common-oss/pom.xml`，依赖 `aliyun-sdk-oss`
- [x] 1.3 在父 `cloud-account-common/pom.xml` 中添加 module

## 2. Core OSS Service

- [x] 2.1 创建 `OssProperties.java` — 配置绑定类（`@ConfigurationProperties(prefix = "oss")`）
- [x] 2.2 创建 `OssUploadService.java` — 预签名 URL 生成，标注 `@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")`
- [x] 2.3 创建 `OssUploadFacade.java` — 门面类，注入 `Optional<OssUploadService>`
- [x] 2.4 创建 `PresignedUrlRequest.java` — 请求 DTO
- [x] 2.5 创建 `PresignedUrlResponse.java` — 响应 VO

## 3. Upload Controller

- [x] 3.1 创建 `UploadController.java` — 通用 `/upload/presigned-url` 接口
- [x] 3.2 添加 content-type 白名单校验逻辑
- [x] 3.3 添加文件大小校验逻辑

## 4. User Avatar API

- [x] 4.1 创建 `UserAvatarController.java` — `/user/avatar/presigned-url` 和 `/user/avatar` 接口
- [x] 4.2 更新 `UserService` — 添加 `updateAvatar(Long userId, String avatar)` 方法
- [x] 4.3 确保 `UserDTO` 已包含 `avatar` 字段

## 5. Dependency Integration

- [x] 5.1 在 `cloud-account-saas-biz-auth/pom.xml` 中添加 `cloud-account-common-oss` 依赖
- [x] 5.2 确认 Nacos 中已添加 OSS 配置项（`oss.enabled`, `oss.endpoint`, `oss.bucket`, `oss.access-key-id`, `oss.access-key-secret`）

## 6. Verification

- [x] 6.1 `mvn clean compile` 编译通过
- [x] 6.2 确认 `@ConditionalOnProperty` 软配置生效（无配置时服务正常启动）
- [x] 6.3 确认调用上传接口时正确返回 presigned URL

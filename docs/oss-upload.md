# OSS 文件上传对接文档

## 概述

系统提供 OSS 预签名 URL 方案，实现**前端直传阿里云 OSS**。后端仅生成签名 URL，前端直接将文件上传到 OSS，减少服务器压力。

```
┌──────────┐     1.获取预签名URL      ┌──────────┐     2.直传OSS      ┌──────────┐
│   前端   │ ──────────────────────► │   网关   │ ────────────────► │   OSS    │
│  浏览器  │                         │          │                   │          │
│          │ ◄───────────────────── │          │                   │          │
└──────────┘     3.返回uploadUrl     └──────────┘                   └──────────┘
```

---

## 流程说明

1. 前端调用后端接口获取预签名上传 URL
2. 前端使用预签名 URL 直接上传文件到 OSS（PUT 请求）
3. 上传成功后，通过返回的 `fileUrl` 访问文件

---

## 接口列表

### 1. 通用文件上传 - 获取预签名 URL

**请求**
```
POST /upload/presigned-url
Content-Type: application/json
```

**请求体**
```json
{
  "filename": "avatar.jpg",
  "contentType": "image/jpeg",
  "size": 102400,
  "type": "avatar"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| filename | string | 是 | 原始文件名 |
| contentType | string | 是 | 文件 MIME 类型 |
| size | long | 是 | 文件大小（字节） |
| type | string | 否 | 文件类型，默认 `file`（可选：`avatar`、`file`、`goods`） |

**响应**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "uploadUrl": "https://java-ai-web-lik.oss-cn-chengdu.aliyuncs.com/avatar/0/0/348e59...jpg?Expires=1780022488&OSSAccessKeyId=xxx&Signature=xxx",
    "fileUrl": "https://java-ai-web-lik.oss-cn-chengdu.aliyuncs.com/avatar/0/0/348e59...jpg",
    "expireSeconds": 300,
    "objectName": "avatar/0/0/348e59...jpg"
  }
}
```

| 字段 | 说明 |
|------|------|
| uploadUrl | 预签名上传地址（用于 PUT 上传，300秒后过期） |
| fileUrl | 文件访问地址（永久可用，前提是 OSS Bucket 已设为公共读） |
| expireSeconds | 预签名 URL 过期时间（秒） |
| objectName | OSS 对象名称（唯一标识） |

---

### 2. 用户头像上传

**请求**
```
POST /user/avatar/presigned-url
Content-Type: application/json
```

**请求体**
```json
{
  "filename": "avatar.jpg",
  "contentType": "image/jpeg",
  "size": 102400
}
```

**说明**
- 自动设置 `type = "avatar"`
- 需要登录后调用（从 Token 中获取 userId）

**响应格式** 同通用上传接口

---

### 3. 更新用户头像

上传成功后调用此接口保存头像地址到用户信息。

**请求**
```
PUT /user/avatar
Content-Type: application/x-www-form-urlencoded
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| avatar | string | 是 | 头像地址（fileUrl） |

**响应**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

## 前端对接示例

### JavaScript / TypeScript

```javascript
/**
 * 获取预签名URL
 */
async function getPresignedUrl(filename, contentType, size, type = 'file') {
  const response = await fetch('/upload/presigned-url', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token  // 如需登录
    },
    body: JSON.stringify({
      filename,
      contentType,
      size,
      type
    })
  });
  const result = await response.json();
  if (result.code !== 200) {
    throw new Error(result.message || '获取上传地址失败');
  }
  return result.data;
}

/**
 * 上传文件到OSS
 */
async function uploadToOss(file, uploadUrl) {
  const response = await fetch(uploadUrl, {
    method: 'PUT',
    headers: {
      'Content-Type': file.type
    },
    body: file
  });
  if (!response.ok) {
    throw new Error('上传失败: ' + response.status);
  }
}

/**
 * 上传用户头像（完整流程）
 */
async function uploadAvatar(file) {
  // 1. 获取预签名URL
  const { uploadUrl, fileUrl } = await getPresignedUrl(
    file.name,
    file.type,
    file.size,
    'avatar'
  );

  // 2. 直传OSS
  await uploadToOss(file, uploadUrl);

  // 3. 更新用户头像
  const updateResponse = await fetch('/user/avatar', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Bearer ' + token
    },
    body: 'avatar=' + encodeURIComponent(fileUrl)
  });

  const result = await updateResponse.json();
  if (result.code !== 200) {
    throw new Error(result.message || '更新头像失败');
  }

  return fileUrl;
}

// 使用示例
const fileInput = document.getElementById('avatar-input');
fileInput.addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (file) {
    try {
      const avatarUrl = await uploadAvatar(file);
      console.log('头像上传成功:', avatarUrl);
    } catch (err) {
      console.error('上传失败:', err.message);
    }
  }
});
```

### Vue 3 示例

```vue
<template>
  <div>
    <input type="file" @change="handleFileChange" accept="image/*" />
    <img v-if="avatarUrl" :src="avatarUrl" alt="头像" />
  </div>
</template>

<script setup>
import { ref } from 'vue';

const avatarUrl = ref('');

async function handleFileChange(e) {
  const file = e.target.files[0];
  if (!file) return;

  try {
    // 1. 获取预签名URL
    const presignedRes = await fetch('/user/avatar/presigned-url', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        filename: file.name,
        contentType: file.type,
        size: file.size
      })
    });
    const { data } = await presignedRes.json();

    // 2. 直传OSS
    await fetch(data.uploadUrl, {
      method: 'PUT',
      headers: { 'Content-Type': file.type },
      body: file
    });

    // 3. 更新头像
    await fetch('/user/avatar', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: 'avatar=' + encodeURIComponent(data.fileUrl)
    });

    avatarUrl.value = data.fileUrl;
  } catch (err) {
    console.error('上传失败:', err);
  }
}
</script>
```

### cURL 测试命令

```bash
# 1. 获取预签名URL
curl -X POST http://localhost:9092/upload/presigned-url \
  -H "Content-Type: application/json" \
  -d '{"filename":"test.jpg","contentType":"image/jpeg","size":102400,"type":"avatar"}'

# 2. 使用预签名URL上传（替换 <UPLOAD_URL> 为上一步返回的 uploadUrl）
curl -X PUT \
  -H "Content-Type: image/jpeg" \
  -T /path/to/your/image.jpg \
  "<UPLOAD_URL>"

# 3. 验证访问
curl -I <FILE_URL>
```

---

## 文件路径规则

OSS 存储路径格式：`{type}/{tenantId}/{userId}/{uuid}.{ext}`

| 路径段 | 说明 |
|--------|------|
| type | 文件类型：`avatar`、`file`、`goods` |
| tenantId | 租户 ID（从 Token 或上下文获取） |
| userId | 用户 ID（从 Token 获取） |
| uuid | 随机 UUID（32位，无中划线） |
| ext | 文件扩展名（小写） |

**示例**
```
avatar/1/100/348e592c1f05474eb690d9bcdbeda2c6.jpg
```

---

## 限制说明

### 文件类型白名单
- `image/jpeg`
- `image/png`
- `image/gif`
- `image/webp`

### 文件大小限制
- 默认最大 **5MB**（可通过 Nacos 配置 `oss.max-file-size` 调整）

### 预签名 URL 有效期
- 默认 **300 秒**（可通过 Nacos 配置 `oss.expire-seconds` 调整）

---

## Nacos 配置说明

在 `common.yml` 中添加以下配置：

```yaml
oss:
  enabled: true                      # 是否启用OSS（false时不加载相关Bean）
  endpoint: oss-cn-chengdu.aliyuncs.com
  bucket: your-bucket-name
  access-key-id: your-access-key-id
  access-key-secret: your-access-key-secret
  cdn-domain: https://your-cdn-domain.com   # 可选，有CDN时配置
  expire-seconds: 300                      # 预签名URL过期时间
  max-file-size: 5242880                   # 最大文件大小（字节）
```

### 配置说明

| 配置项 | 必填 | 说明 |
|--------|------|------|
| oss.enabled | 否 | 默认 false，为 true 时加载 OssUploadService |
| oss.endpoint | 是 | OSS Endpoint |
| oss.bucket | 是 | Bucket 名称 |
| oss.access-key-id | 是 | 阿里云 AccessKey ID |
| oss.access-key-secret | 是 | 阿里云 AccessKey Secret |
| oss.cdn-domain | 否 | CDN 域名，配置后 fileUrl 返回 CDN 地址 |
| oss.expire-seconds | 否 | 默认 300 |
| oss.max-file-size | 否 | 默认 5242880（5MB） |

---

## OSS Bucket 权限要求

为确保 `fileUrl` 可以直接访问，需要将 OSS Bucket 设置为**公共读**：

1. 登录阿里云 OSS 控制台
2. 选择目标 Bucket
3. 进入 **基础设置** → **权限管理**
4. 设置 **Bucket 权限** 为 **公共读**

---

## 错误码说明

| code | 说明 | 解决方案 |
|------|------|----------|
| 200 | 成功 | - |
| 400 | 参数错误 | 检查 filename、contentType、size 必填参数 |
| 500 | 服务器错误 | 联系管理员 |
| OSS未配置 | OSS未启用 | 检查 Nacos 配置 `oss.enabled=true` |
| 不支持的文件类型 | contentType 不在白名单 | 仅支持 jpeg、png、gif、webp |
| 文件大小超出限制 | 超过 max-file-size | 压缩文件或联系管理员调整限制 |

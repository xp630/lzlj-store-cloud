# OpenAPI 对接文档

## 概述

本文档描述如何通过 OpenAPI 接口安全地接入系统，支持第三方系统与本平台进行数据交互。

---

## 认证流程

```
第三方系统                                    网关                              业务服务
    │                                          │                                  │
    │  1. 携带签名请求                          │                                  │
    │ ─────────────────────────────────────►  │                                  │
    │                                          │  2. 验证签名                      │
    │                                          │  3. 提取 tenant_id               │
    │                                          │  4. 转发请求                     │
    │                                          │ ──────────────────────────────►  │
    │                                          │                                  │
    │  5. 返回响应                              │  6. 返回响应                    │
    │ ◄─────────────────────────────────────── │ ◄────────────────────────────── │
```

---

## 第一步：获取 API 密钥

联系平台管理员，获取以下信息：
- **API Key**：公钥，用于标识身份（格式：`ak_xxx`）
- **API Secret**：密钥，用于签名（格式：`sk_xxx`），**仅在创建时返回，请妥善保管**

---

## 第二步：生成签名

### 签名算法

1. **HMAC-SHA256** 算法
2. **Base64** 编码

### 签名字符串格式

```
{timestamp}\n{method}\n{path}\n{body}
```

| 字段 | 说明 | 示例 |
|------|------|------|
| timestamp | 时间戳（毫秒） | `1716800000000` |
| method | HTTP 方法（大写） | `POST` |
| path | 请求路径 | `/api/order` |
| body | 请求体 JSON，无 body 时为空字符串 | `{"name":"test"}` |

### Java 示例

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ApiSignature {

    public static String sign(long timestamp, String method, String path, String body, String secret) {
        // 1. 构建签名字符串
        String data = timestamp + "\n" + method.toUpperCase() + "\n" + path + "\n" + (body != null ? body : "");

        // 2. HMAC-SHA256
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (Exception e) {
            throw new RuntimeException("签名失败", e);
        }
    }
}
```

### Python 示例

```python
import hmac
import hashlib
import base64
import time
import json

def sign(timestamp, method, path, body, secret):
    # 1. 构建签名字符串
    data = f"{timestamp}\n{method.upper()}\n{path}\n{body or ''}"

    # 2. HMAC-SHA256 + Base64
    signature = hmac.new(
        secret.encode('utf-8'),
        data.encode('utf-8'),
        hashlib.sha256
    ).digest()
    return base64.b64encode(signature).decode('utf-8')

# 使用示例
timestamp = int(time.time() * 1000)
body = json.dumps({"name": "test"})
signature = sign(timestamp, "POST", "/api/order", body, "sk_your_secret")
```

---

## 第三步：发送请求

### 请求 Header

| Header | 必填 | 说明 |
|--------|------|------|
| X-API-Key | 是 | API 公钥（格式：`ak_xxx`） |
| X-Timestamp | 是 | 时间戳（毫秒） |
| X-Signature | 是 | 签名结果（Base64） |
| Content-Type | 是 | `application/json` |

### 示例请求

```bash
curl -X POST 'https://api.example.com/api/order' \
  -H 'Content-Type: application/json' \
  -H 'X-API-Key: ak_a1b2c3d4e5f6g7h8i9j0' \
  -H 'X-Timestamp: 1716800000000' \
  -H 'X-Signature: K8vJ2h7Z9...=' \
  -d '{"product_id": 1, "quantity": 2}'
```

### 签名有效期

- 时间戳与服务器时间差 **不能超过 5 分钟**
- 超过则判定为重放攻击，返回 `401 Unauthorized`

---

## 响应格式

### 成功响应

```json
{
  "code": 200,
  "data": {
    "order_id": 12345,
    "status": "created"
  },
  "message": "操作成功"
}
```

### 错误响应

```json
{
  "code": 401,
  "data": null,
  "message": "签名验证失败"
}
```

### 错误码说明

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 签名验证失败 / API Key 无效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁（超出速率限制） |
| 500 | 服务器内部错误 |

---

## 速率限制

- 默认限制：**100 次/分钟**
- 可根据需求联系管理员调整
- 超出限制返回 `429 Too Many Requests`

---

## 错误排查

### 1. 签名不匹配
- 检查 timestamp 是否正确（毫秒）
- 检查 method 是否大写
- 检查 body 是否与签名时一致（无 body 传空字符串）

### 2. 401 Unauthorized
- 检查 API Key 是否正确
- 检查时间戳是否在 5 分钟内
- 检查签名算法是否正确

### 3. 429 Too Many Requests
- 降低请求频率
- 联系管理员提高速率限制

---

## 附录：完整调用示例

### Java

```java
import java.net.http.*;
import java.nio.charset.StandardCharsets.*;
import java.time.Duration;

public class ApiClient {

    private final String apiKey;
    private final String apiSecret;
    private final String baseUrl;

    public ApiClient(String apiKey, String apiSecret, String baseUrl) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.baseUrl = baseUrl;
    }

    public String post(String path, String body) throws Exception {
        long timestamp = System.currentTimeMillis();
        String signature = sign(timestamp, "POST", path, body, apiSecret);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Content-Type", "application/json")
            .header("X-API-Key", apiKey)
            .header("X-Timestamp", String.valueOf(timestamp))
            .header("X-Signature", signature)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .timeout(Duration.ofSeconds(30))
            .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // sign 方法见上方签名示例
}
```

### Python

```python
import requests
import time
import json

class ApiClient:

    def __init__(self, api_key, api_secret, base_url):
        self.api_key = api_key
        self.api_secret = api_secret
        self.base_url = base_url

    def post(self, path, data):
        timestamp = int(time.time() * 1000)
        body = json.dumps(data)
        signature = sign(timestamp, "POST", path, body, self.api_secret)

        response = requests.post(
            self.base_url + path,
            headers={
                "Content-Type": "application/json",
                "X-API-Key": self.api_key,
                "X-Timestamp": str(timestamp),
                "X-Signature": signature
            },
            data=body
        )
        return response.json()

# 使用示例
client = ApiClient(
    api_key="ak_a1b2c3d4e5f6g7h8i9j0",
    api_secret="sk_x9y8z7w6v5u4t3s2r1q0",
    base_url="https://api.example.com"
)

result = client.post("/api/order", {"product_id": 1, "quantity": 2})
print(result)
```

---

## 联系方式

如有疑问，请联系平台技术支持。

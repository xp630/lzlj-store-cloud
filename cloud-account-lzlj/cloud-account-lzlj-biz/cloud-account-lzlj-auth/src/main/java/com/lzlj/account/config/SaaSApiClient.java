package com.lzlj.account.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.common.core.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * SaaS 服务 HTTP 客户端
 * 用于 LZLJ 调用 SaaS 服务的 OpenAPI
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaaSApiClient {

    private final SaaSApiConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String HEADER_API_KEY = "X-API-Key";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";
    private static final String HEADER_SIGNATURE = "X-Signature";

    /**
     * GET 请求
     */
    public <T> Result<T> get(String path, Class<T> responseType) {
        return request(path, null, HttpMethod.GET, responseType);
    }

    /**
     * POST 请求
     */
    public <T> Result<T> post(String path, Object body, Class<T> responseType) {
        return request(path, body, HttpMethod.POST, responseType);
    }

    /**
     * PUT 请求
     */
    public <T> Result<T> put(String path, Object body, Class<T> responseType) {
        return request(path, body, HttpMethod.PUT, responseType);
    }

    /**
     * DELETE 请求
     */
    public <T> Result<T> delete(String path, Class<T> responseType) {
        return request(path, null, HttpMethod.DELETE, responseType);
    }

    /**
     * 通用请求方法
     */
    @SuppressWarnings("unchecked")
    private <T> Result<T> request(String path, Object body, HttpMethod method, Class<T> responseType) {
        if (!config.isEnabled()) {
            log.warn("SaaS API 调用已禁用");
            return Result.fail("SaaS 服务未启用");
        }

        String url = config.getBaseUrl() + "/openapi" + path;
        log.info("调用 SaaS API: {} {}", method, url);

        // 构建 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 添加鉴权信息
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = generateSignature(method.name(), path, timestamp);

        headers.add(HEADER_API_KEY, config.getApiKey());
        headers.add(HEADER_TIMESTAMP, timestamp);
        headers.add(HEADER_SIGNATURE, signature);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    String.class
            );

            log.debug("SaaS API 响应状态: {}", response.getStatusCode());

            if (response.getBody() == null || response.getBody().isEmpty()) {
                return Result.fail("SaaS 服务返回为空");
            }

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            Integer code = (Integer) responseMap.get("code");
            String message = (String) responseMap.get("message");
            Object data = responseMap.get("data");

            if (code != null && code == 200) {
                // 成功响应，反序列化 data 字段
                if (data != null && responseType != null) {
                    T resultData = objectMapper.convertValue(data, responseType);
                    return Result.success(message, resultData);
                }
                return Result.success(message, null);
            } else {
                return Result.fail(code != null ? code : 500, message != null ? message : "调用失败");
            }

        } catch (RestClientException e) {
            log.error("调用 SaaS API 网络异常: {} {}", method, url, e);
            return Result.fail("调用 SaaS 服务网络异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("调用 SaaS API 失败: {} {}", method, url, e);
            return Result.fail("调用 SaaS 服务异常: " + e.getMessage());
        }
    }

    /**
     * 生成签名
     * 签名算法：SHA256(apiKey + timestamp + secret)
     */
    private String generateSignature(String method, String path, String timestamp) {
        return CryptoUtils.generateSignature(config.getApiKey(), timestamp, config.getApiSecret());
    }

    /**
     * 获取商户详情（根据编码）
     */
    public <T> Result<T> getMerchantByCode(String merchantCode, Class<T> responseType) {
        return get("/merchant/code/" + merchantCode, responseType);
    }

    /**
     * 分页查询商户
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键字（可选，模糊搜索商户名称或编码）
     * @param status 商户状态（可选）
     */
    @SuppressWarnings("unchecked")
    public Result<Map> getMerchants(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        StringBuilder path = new StringBuilder("/merchant/page?pageNum=").append(pageNum).append("&pageSize=").append(pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            path.append("&keyword=").append(keyword);
        }
        if (status != null) {
            path.append("&status=").append(status);
        }
        return (Result<Map>) get(path.toString(), Map.class);
    }
}

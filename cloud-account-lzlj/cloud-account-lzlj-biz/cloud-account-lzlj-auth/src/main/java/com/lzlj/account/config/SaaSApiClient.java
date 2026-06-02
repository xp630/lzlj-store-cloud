package com.lzlj.account.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.common.core.utils.SignatureUtils;
import com.lzlj.account.merchant.dto.MerchantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SaaS 服务 HTTP 客户端
 * 用于 LZLJ 调用 SaaS 服务的 OpenAPI
 * 统一使用 POST + JSON Body
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaaSApiClient {

    private final SaaSApiConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final String HEADER_API_KEY = "X-API-Key";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";
    private static final String HEADER_SIGNATURE = "X-Signature";

    // ==================== 商户接口 ====================

    /**
     * 获取商户详情（ID）
     */
    public Result<MerchantDTO> getMerchantById(Long id) {
        Map<String,Long> param = new HashMap<>();
        param.put("id",id);
        return post("/merchant/getById", param, MerchantDTO.class);
    }

    /**
     * 获取商户详情（编码）
     */
    public Result<MerchantDTO> getMerchantByCode(String merchantCode) {
        Map<String,String> param = new HashMap<>();
        param.put("merchantCode",merchantCode);
        return post("/merchant/getByCode", param, MerchantDTO.class);
    }

    /**
     * 分页查询商户
     */
    public Result<PageResult<MerchantDTO>> getMerchants(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Map<String, Object> body = buildMerchantPageBody(pageNum, pageSize, keyword, status);
        return post("/merchant/page", body, new TypeReference<PageResult<MerchantDTO>>() {});
    }

    // ==================== 支付通道接口 ====================

    /**
     * 获取支付通道详情（ID）
     */
    public Result<PaymentChannelDTO> getPaymentChannelById(Long id) {
        Map<String,Long> param = new HashMap<>();
        param.put("id",id);
        return post("/paymentChannel/getById", param, PaymentChannelDTO.class);
    }

    /**
     * 分页查询支付通道
     */
    public Result<PageResult<PaymentChannelDTO>> getPaymentChannels(Integer pageNum, Integer pageSize, String channelName, Integer status) {
        Map<String, Object> body = buildPaymentChannelPageBody(pageNum, pageSize, channelName, status);
        return post("/paymentChannel/page", body, new TypeReference<PageResult<PaymentChannelDTO>>() {});
    }

    /**
     * 查询支付通道列表
     */
    public Result<List<PaymentChannelDTO>> listPaymentChannels(Integer status) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        if (status != null) {
            body.put("status", status);
        }
        return post("/paymentChannel/list", body, new TypeReference<List<PaymentChannelDTO>>() {});
    }

    // ==================== 内部方法 ====================

    private Map<String, Object> buildMerchantPageBody(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("pageNum", pageNum);
        body.put("pageSize", pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            body.put("keyword", keyword);
        }
        if (status != null) {
            body.put("status", status);
        }
        return body;
    }

    private Map<String, Object> buildPaymentChannelPageBody(Integer pageNum, Integer pageSize, String channelName, Integer status) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("pageNum", pageNum);
        body.put("pageSize", pageSize);
        if (channelName != null && !channelName.isEmpty()) {
            body.put("channelName", channelName);
        }
        if (status != null) {
            body.put("status", status);
        }
        return body;
    }

    /**
     * POST 请求（支持 Class 类型）
     */
    private <T> Result<T> post(String path, Object body, Class<T> responseType) {
        return post(path, body, new TypeReference<T>() {
            @Override
            public java.lang.reflect.Type getType() {
                return responseType;
            }
        });
    }

    /**
     * POST 请求（支持 TypeReference 类型）
     */
    private <T> Result<T> post(String path, Object body, TypeReference<T> typeRef) {
        if (!config.isEnabled()) {
            log.warn("SaaS API 调用已禁用");
            return Result.fail("SaaS 服务未启用");
        }

        String url = config.getBaseUrl() + "/openapi/saas-auth/openapi" + path;
        String fullPath = "/openapi/saas-auth/openapi" + path;
        log.info("调用 SaaS API: POST {}", url);

        // 构建 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 添加鉴权信息
        String timestamp = String.valueOf(System.currentTimeMillis());
        String bodyStr = serializeBody(body);
        String signature = generateSignature(fullPath, timestamp, bodyStr);
        log.info("DEBUG 签名: path={}, timestamp={}, body={}, signature={}", fullPath, timestamp, bodyStr, signature);

        headers.add(HEADER_API_KEY, config.getApiKey());
        headers.add(HEADER_TIMESTAMP, timestamp);
        headers.add(HEADER_SIGNATURE, signature);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
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
                if (data != null) {
                    T resultData = objectMapper.convertValue(data, typeRef);
                    return Result.success(message, resultData);
                }
                return Result.success(message, null);
            } else {
                return Result.fail(code != null ? code : 500, message != null ? message : "调用失败");
            }

        } catch (RestClientException e) {
            log.error("调用 SaaS API 网络异常: POST {}", url, e);
            return Result.fail("调用 SaaS 服务网络异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("调用 SaaS API 失败: POST {}", url, e);
            return Result.fail("调用 SaaS 服务异常: " + e.getMessage());
        }
    }

    private String serializeBody(Object body) {
        if (body == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            log.warn("序列化请求体失败", e);
            return "";
        }
    }

    /**
     * 生成签名
     */
    private String generateSignature(String path, String timestamp, String body) {
        String secret = new String(Base64.getDecoder().decode(config.getApiSecret()));
        return SignatureUtils.sign(Long.parseLong(timestamp), "POST", path, body, secret);
    }
}

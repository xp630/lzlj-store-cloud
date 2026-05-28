package com.lzlj.account.gateway.filter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * API签名工具类
 * 用于网关验签和客户端签名
 */
public class SignatureUtils {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 生成签名
     *
     * @param timestamp 时间戳（毫秒）
     * @param method    HTTP方法（GET/POST/PUT/DELETE）
     * @param path      请求路径（如 /api/user）
     * @param body      请求体（无body时传空字符串或null）
     * @param secret    API密钥
     * @return Base64编码的签名
     */
    public static String sign(long timestamp, String method, String path, String body, String secret) {
        String data = buildSignData(timestamp, method, path, body);
        return hmacSha256(data, secret);
    }

    /**
     * 验证签名
     *
     * @param timestamp     时间戳
     * @param method        HTTP方法
     * @param path          请求路径
     * @param body          请求体
     * @param secret        API密钥
     * @param signature     待验证的签名
     * @param expireSeconds 签名有效期（秒），防止重放攻击
     * @return true=验签通过
     */
    public static boolean verify(long timestamp, String method, String path, String body,
                                 String secret, String signature, long expireSeconds) {
        // 1. 验证时间戳，防止重放
        long now = System.currentTimeMillis();
        if (Math.abs(now - timestamp) > expireSeconds * 1000) {
            return false;
        }

        // 2. 计算签名并比对
        String expectedSign = sign(timestamp, method, path, body, secret);
        return constantTimeEquals(expectedSign, signature);
    }

    /**
     * 构建签名字符串
     */
    private static String buildSignData(long timestamp, String method, String path, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append("\n");
        sb.append(method.toUpperCase()).append("\n");
        sb.append(path).append("\n");
        sb.append(body != null ? body : "");
        return sb.toString();
    }

    /**
     * HMAC-SHA256 加密
     */
    private static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256加密失败", e);
        }
    }

    /**
     * 恒定时间比较，防止时序攻击
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}

package com.lzlj.account.common.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加解密工具类
 */
public class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * SHA256 哈希
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA256 加密失败", e);
        }
    }

    /**
     * MD5 哈希
     */
    public static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }

    /**
     * Base64 编码
     */
    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码
     */
    public static String base64Decode(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

    /**
     * 生成签名
     * 签名算法：SHA256(apiKey + timestamp + secret)
     */
    public static String generateSignature(String apiKey, String timestamp, String secret) {
        String raw = apiKey + timestamp + secret;
        return sha256(raw);
    }
}

package com.lzlj.account.common.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 签名工具类测试
 */
class SignatureUtilsTest {

    @Test
    void testSignAndVerify() {
        long timestamp = System.currentTimeMillis();
        String method = "POST";
        String path = "/api/order";
        String body = "{\"product_id\": 1}";
        String secret = "sk_test_secret_123";

        // 生成签名
        String signature = SignatureUtils.sign(timestamp, method, path, body, secret);
        assertNotNull(signature);

        // 验签成功
        boolean ok = SignatureUtils.verify(timestamp, method, path, body, secret, signature, 300);
        assertTrue(ok);
    }

    @Test
    void testVerifyFailed() {
        long timestamp = System.currentTimeMillis();
        String method = "POST";
        String path = "/api/order";
        String body = "{\"product_id\": 1}";
        String secret = "sk_test_secret_123";
        String wrongSecret = "sk_wrong_secret";

        // 生成签名
        String signature = SignatureUtils.sign(timestamp, method, path, body, secret);

        // 用错误secret验签失败
        boolean ok = SignatureUtils.verify(timestamp, method, path, body, wrongSecret, signature, 300);
        assertFalse(ok);
    }

    @Test
    void testVerifyExpired() throws InterruptedException {
        long timestamp = System.currentTimeMillis() - 600000; // 10分钟前
        String method = "GET";
        String path = "/api/user";
        String body = "";
        String secret = "sk_test";

        // 生成签名
        String signature = SignatureUtils.sign(timestamp, method, path, body, secret);

        // 5分钟有效期，已过期，验签失败
        boolean ok = SignatureUtils.verify(timestamp, method, path, body, secret, signature, 300);
        assertFalse(ok);
    }

    @Test
    void testSignWithoutBody() {
        long timestamp = System.currentTimeMillis();
        String method = "GET";
        String path = "/api/user/1";
        String secret = "sk_test";

        String signature = SignatureUtils.sign(timestamp, method, path, null, secret);
        assertNotNull(signature);

        boolean ok = SignatureUtils.verify(timestamp, method, path, null, secret, signature, 300);
        assertTrue(ok);
    }
}

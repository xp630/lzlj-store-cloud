package com.lzlj.account.common.api.feign;

import com.lzlj.account.common.api.feign.fallback.LzljUserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;

/**
 * LZLJ 用户服务 Feign 客户端
 */
@FeignClient(
        name = "lzlj-auth",
        path = "/user",
        fallback = LzljUserFeignClientFallback.class
)
public interface LzljUserFeignClient {

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    LzljUserInfo getById(@PathVariable("id") Long id);

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    LzljUserInfo getCurrentUser();

    /**
     * LZLJ 用户信息DTO（无 tenantId，有 orgId）
     */
    class LzljUserInfo implements Serializable {
        private Long id;
        private String username;
        private String realName;
        private String phone;
        private String email;
        private Long orgId;

        public LzljUserInfo() {
        }

        public LzljUserInfo(Long id, String username, String realName, String phone, String email, Long orgId) {
            this.id = id;
            this.username = username;
            this.realName = realName;
            this.phone = phone;
            this.email = email;
            this.orgId = orgId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Long getOrgId() {
            return orgId;
        }

        public void setOrgId(Long orgId) {
            this.orgId = orgId;
        }
    }
}

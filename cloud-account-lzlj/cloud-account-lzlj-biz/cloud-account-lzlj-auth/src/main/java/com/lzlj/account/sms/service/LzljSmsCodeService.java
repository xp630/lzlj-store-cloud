package com.lzlj.account.sms.service;

/**
 * 短信验证码服务接口
 */
public interface LzljSmsCodeService {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param type 类型: login/register/reset_pwd
     * @return 生成的验证码
     */
    String sendCode(String phone, String type);

    /**
     * 验证短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 类型
     * @return 验证通过返回true
     */
    boolean verifyCode(String phone, String code, String type);

    /**
     * 标记验证码已使用
     * @param phone 手机号
     * @param code 验证码
     * @param type 类型
     */
    void markAsUsed(String phone, String code, String type);
}

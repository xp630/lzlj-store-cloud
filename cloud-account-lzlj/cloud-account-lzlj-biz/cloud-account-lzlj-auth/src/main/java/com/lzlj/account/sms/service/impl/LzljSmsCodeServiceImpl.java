package com.lzlj.account.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.sms.dao.LzljSmsCodeDao;
import com.lzlj.account.sms.entity.LzljSmsCode;
import com.lzlj.account.sms.service.LzljSmsCodeService;
import com.lzlj.account.systemparameter.dto.LzljSystemParameterDTO;
import com.lzlj.account.systemparameter.service.LzljSystemParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

/**
 * 短信验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljSmsCodeServiceImpl implements LzljSmsCodeService {

    private final LzljSmsCodeDao smsCodeDao;
    private final LzljSystemParameterService systemParameterService;

    /**
     * 验证码有效期：5分钟
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 每日发送次数限制参数key
     */
    private static final String SMS_DAILY_LIMIT_KEY = "sms_send_daily_limit";

    /**
     * 默认每日限制次数
     */
    private static final int DEFAULT_DAILY_LIMIT = 5;

    @Override
    public String sendCode(String phone, String type) {
        // 检查每日发送次数限制
        checkDailyLimit(phone);

        // 生成6位数字验证码
        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireTime = now.plusMinutes(CODE_EXPIRE_MINUTES);

        // 创建验证码记录
        LzljSmsCode smsCode = new LzljSmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setExpireTime(expireTime);
        smsCode.setStatus(0); // 未使用
        smsCode.setCreatedAt(now);

        smsCodeDao.insert(smsCode);
        log.info("发送短信验证码成功: phone={}, code={}, type={}", phone, code, type);

        // TODO: 实际发送短信逻辑由第三方处理，这里仅记录验证码
        return code;
    }

    /**
     * 检查每日发送次数限制
     */
    private void checkDailyLimit(String phone) {
        int dailyLimit = getDailyLimit();
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        LambdaQueryWrapper<LzljSmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSmsCode::getPhone, phone)
               .eq(LzljSmsCode::getType, "login")
               .ge(LzljSmsCode::getCreatedAt, startOfDay)
               .le(LzljSmsCode::getCreatedAt, endOfDay);

        long count = smsCodeDao.selectCount(wrapper);
        if (count >= dailyLimit) {
            throw new BusinessException(ResultCode.SMS_DAILY_LIMIT_EXCEEDED);
        }
    }

    /**
     * 获取每日发送次数限制
     */
    private int getDailyLimit() {
        try {
            LzljSystemParameterDTO param = systemParameterService.getByKey(SMS_DAILY_LIMIT_KEY);
            if (param != null && param.getParamValue() != null && !param.getParamValue().isEmpty()) {
                return Integer.parseInt(param.getParamValue());
            }
        } catch (Exception e) {
            log.warn("获取短信每日发送限制失败，使用默认值: {}", e.getMessage());
        }
        return DEFAULT_DAILY_LIMIT;
    }

    @Override
    public boolean verifyCode(String phone, String code, String type) {
        // 查询最新未使用的验证码
        LambdaQueryWrapper<LzljSmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSmsCode::getPhone, phone)
               .eq(LzljSmsCode::getCode, code)
               .eq(LzljSmsCode::getType, type)
               .eq(LzljSmsCode::getStatus, 0) // 未使用
               .orderByDesc(LzljSmsCode::getCreatedAt)
               .last("LIMIT 1");

        LzljSmsCode smsCode = smsCodeDao.selectOne(wrapper);

        if (smsCode == null) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 检查是否过期
        if (LocalDateTime.now().isAfter(smsCode.getExpireTime())) {
            // 标记为已过期
            smsCode.setStatus(2);
            smsCodeDao.updateById(smsCode);
            throw new BusinessException(ResultCode.VERIFY_CODE_EXPIRED);
        }

        return true;
    }

    @Override
    public void markAsUsed(String phone, String code, String type) {
        LambdaQueryWrapper<LzljSmsCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSmsCode::getPhone, phone)
               .eq(LzljSmsCode::getCode, code)
               .eq(LzljSmsCode::getType, type)
               .eq(LzljSmsCode::getStatus, 0)
               .orderByDesc(LzljSmsCode::getCreatedAt)
               .last("LIMIT 1");

        LzljSmsCode smsCode = smsCodeDao.selectOne(wrapper);
        if (smsCode != null) {
            smsCode.setStatus(1); // 已使用
            smsCode.setUsedAt(LocalDateTime.now());
            smsCodeDao.updateById(smsCode);
        }
    }

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}

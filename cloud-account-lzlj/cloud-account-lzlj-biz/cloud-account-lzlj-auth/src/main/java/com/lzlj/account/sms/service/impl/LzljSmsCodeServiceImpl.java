package com.lzlj.account.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.sms.dao.LzljSmsCodeDao;
import com.lzlj.account.sms.entity.LzljSmsCode;
import com.lzlj.account.sms.service.LzljSmsCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 短信验证码服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljSmsCodeServiceImpl implements LzljSmsCodeService {

    private final LzljSmsCodeDao smsCodeDao;

    /**
     * 验证码有效期：5分钟
     */
    private static final int CODE_EXPIRE_MINUTES = 5;

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;

    @Override
    public String sendCode(String phone, String type) {
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

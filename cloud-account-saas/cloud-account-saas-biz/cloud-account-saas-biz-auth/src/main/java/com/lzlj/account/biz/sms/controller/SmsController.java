package com.lzlj.account.biz.sms.controller;

import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.sms.dto.SendSmsRequest;
import com.lzlj.account.biz.sms.service.SmsCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 短信控制器
 */
@Tag(name = "短信管理")
@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsCodeService smsCodeService;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/send")
    public Result<Void> sendSms(@Valid @RequestBody SendSmsRequest request) {
        smsCodeService.sendCode(request.getPhone(), request.getType());
        return Result.success();
    }
}

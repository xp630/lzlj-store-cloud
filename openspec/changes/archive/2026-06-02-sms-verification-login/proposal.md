## Why

用户登录场景需要增强安全校验，在账号密码基础上增加短信验证码二次验证。短信发送由第三方处理，但验证码校验逻辑需要自主实现。同时支持白名单机制，允许特定账号跳过短信验证。

## What Changes

- 新增短信验证码表 `sms_code`，存储验证码及状态
- 新增短信验证码发送接口 `POST /sms/send`
  - 输入: phone
  - 逻辑: 生成6位验证码，写入数据库，过期时间5分钟
  - 不实际调用第三方短信服务
- 登录接口 `POST /user/login` 改为双重验证模式
  - 输入: username + password + smsCode
  - 账号默认就是手机号（username = phone）
  - 密码验证成功后，校验短信验证码
  - 白名单用户（配置在系统参数中）传入 `smsCode=0000` 可跳过短信验证
- 新增系统参数 `sms_login_whitelist`
  - 值格式: 逗号分隔的手机号列表
  - 例: `13800138000,13900139000`
- SaaS 和 LZLJ 两个模块都要实现相同功能

## Capabilities

### New Capabilities

- `sms-verification-login`: 短信验证码双重登录功能
  - 验证码发送接口
  - 双重验证登录接口（密码+短信）
  - 验证码表及 CRUD
  - 白名单跳过逻辑

### Modified Capabilities

- `framework-entity`: 用户实体已有 phone 字段，登录逻辑使用 username 作为手机号查询
- `framework-service`: 用户登录服务需支持密码+短信双重验证模式
- `system-parameter`: 新增 `sms_login_whitelist` 参数用于配置白名单

## Impact

- **数据库**: 新增 `sms_code` 表
- **系统参数**: 新增 `sms_login_whitelist` 配置项
- **SaaS 模块**:
  - `cloud-account-saas-biz-auth`: SmsCodeEntity, SmsCodeDao, SmsCodeService, SmsController, UserService.login() 修改
- **LZLJ 模块**:
  - `cloud-account-lzlj-biz-auth`: 同上结构
- **公共模块**:
  - `cloud-account-common-core`: 可能需要新增验证码相关 ResultCode

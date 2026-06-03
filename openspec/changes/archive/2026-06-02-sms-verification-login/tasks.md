## 1. Database Schema

- [x] 1.1 Create SMS code table SQL script for SaaS
- [x] 1.2 Create SMS code table SQL script for LZLJ

## 2. SaaS Module Implementation

- [x] 2.1 Create SmsCode entity class
- [x] 2.2 Create SmsCodeDao interface
- [x] 2.3 Create SmsCodeService interface
- [x] 2.4 Create SmsCodeServiceImpl with send and verify logic
- [x] 2.5 Create SmsController with /sms/send endpoint
- [x] 2.6 Create SendSmsRequest DTO
- [x] 2.7 Add @Schema annotations to DTOs
- [x] 2.8 Modify UserServiceImpl.login() to support dual verification (password + SMS)
- [x] 2.9 Add whitelist check logic in login service

## 3. LZLJ Module Implementation

- [x] 3.1 Create SmsCode entity class (LzljSmsCode)
- [x] 3.2 Create SmsCodeDao interface (LzljSmsCodeDao)
- [x] 3.3 Create SmsCodeService interface
- [x] 3.4 Create SmsCodeServiceImpl with send and verify logic
- [x] 3.5 Create SmsController with /sms/send endpoint
- [x] 3.6 Create SendSmsRequest DTO
- [x] 3.7 Add @Schema annotations to DTOs
- [x] 3.8 Modify LzljUserServiceImpl.login() to support dual verification (password + SMS)
- [x] 3.9 Add whitelist check logic in login service

## 4. Common Module Updates

- [ ] 4.1 Add SMS verification error codes to ResultCode if needed

## 5. System Parameter

- [ ] 5.1 Configure sms_login_whitelist system parameter with example values

## 6. Testing

- [ ] 6.1 Test SMS send endpoint
- [ ] 6.2 Test dual verification login flow (password + SMS)
- [ ] 6.3 Test expired code rejection
- [ ] 6.4 Test reused code rejection
- [ ] 6.5 Test whitelist bypass with smsCode="0000"
- [ ] 6.6 Test whitelist user must still provide valid code (not 0000)
- [ ] 6.7 Test non-whitelist user cannot use 0000 to bypass

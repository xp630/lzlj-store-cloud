# SMS Verification Login

## ADDED Requirements

### Requirement: Send SMS Verification Code
The system SHALL provide an endpoint to send SMS verification code. The code SHALL be generated, stored in database, and marked with expiration time.

#### Scenario: Send verification code successfully
- **WHEN** user sends POST /sms/send with valid phone number
- **THEN** system generates a 6-digit code, stores it in database with 5-minute expiration, and returns success

#### Scenario: Send verification code with invalid phone
- **WHEN** user sends POST /sms/send with invalid phone format
- **THEN** system returns validation error

#### Scenario: Send verification code without phone
- **WHEN** user sends POST /sms/send without phone parameter
- **THEN** system returns validation error

### Requirement: Verify SMS Code
The system SHALL verify the SMS code provided during login. The code MUST be valid, not expired, and not already used.

#### Scenario: Verify valid SMS code
- **WHEN** user provides correct phone and valid SMS code
- **THEN** system marks the code as used, finds user by phone, and returns authentication token

#### Scenario: Verify expired SMS code
- **WHEN** user provides correct phone but SMS code has expired
- **THEN** system returns error indicating code expired

#### Scenario: Verify already used SMS code
- **WHEN** user provides correct phone but SMS code has already been used
- **THEN** system returns error indicating code already used

#### Scenario: Verify invalid SMS code
- **WHEN** user provides correct phone but wrong SMS code
- **THEN** system returns error indicating code invalid

### Requirement: Dual Verification Login
The system SHALL require both password verification and SMS code verification for login. The username field equals phone number.

#### Scenario: Login with password and SMS code successfully
- **WHEN** user sends POST /user/login with username (phone), password, and valid smsCode
- **THEN** system validates password first, then validates SMS code, and returns JWT token

#### Scenario: Login with password verification fails
- **WHEN** user sends POST /user/login with wrong password
- **THEN** system returns password error without checking SMS code

#### Scenario: Login with whitelist account and bypass code
- **WHEN** user is in sms_login_whitelist and sends smsCode as "0000"
- **THEN** system skips SMS code verification after password success and returns JWT token

#### Scenario: Login with whitelist account but wrong bypass code
- **WHEN** user is in sms_login_whitelist but sends wrong smsCode (not "0000")
- **THEN** system validates SMS code normally and returns error if code is invalid

#### Scenario: Login with non-whitelist account must verify SMS
- **WHEN** user is NOT in sms_login_whitelist and sends smsCode "0000"
- **THEN** system validates SMS code normally and returns error (0000 is not a valid code)

### Requirement: SMS Login Whitelist
The system SHALL check sms_login_whitelist system parameter to determine if SMS verification can be skipped.

#### Scenario: Check whitelist for whitelist user
- **WHEN** user phone is in system parameter sms_login_whitelist
- **THEN** user can skip SMS verification by sending smsCode="0000"

#### Scenario: Check whitelist for non-whitelist user
- **WHEN** user phone is NOT in system parameter sms_login_whitelist
- **THEN** user must provide valid SMS code

#### Scenario: Whitelist parameter not configured
- **WHEN** system parameter sms_login_whitelist is not set or empty
- **THEN** all users must provide valid SMS code

### Requirement: SMS Code Data Model
The SMS verification code entity SHALL contain the following fields:

- **id**: Primary key
- **phone**: Phone number (indexed)
- **code**: 6-digit verification code
- **type**: Code type (login/register/reset_pwd)
- **expire_time**: Expiration datetime
- **status**: Code status (0=unused, 1=used, 2=expired)
- **created_at**: Creation timestamp
- **used_at**: Usage timestamp (when code was used)

#### Scenario: SMS code status transitions
- **WHEN** code is created
- **THEN** status is 0 (unused)

- **WHEN** valid code is verified
- **THEN** status becomes 1 (used) and used_at is set

- **WHEN** code expiration time passes
- **THEN** status becomes 2 (expired)

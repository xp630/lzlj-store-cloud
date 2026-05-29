## ADDED Requirements

### Requirement: Payment Channel CRUD Operations

The system SHALL support basic CRUD operations for payment channels including create, read, update, delete, and paginated list queries.

#### Scenario: Create payment channel with valid data

- **WHEN** admin sends POST /payment-channel with valid channelCode, channelName, paymentMethod and fee fields
- **THEN** the system SHALL create a new payment channel record
- **AND** return the created channel with id

#### Scenario: Query payment channel by id

- **WHEN** admin sends GET /payment-channel/{id}
- **AND** the channel exists
- **THEN** the system SHALL return the channel details
- **AND** include all fields including channelCode, channelName, paymentMethod and fee fields

#### Scenario: Update payment channel

- **WHEN** admin sends PUT /payment-channel/{id} with updated fee data
- **AND** the channel exists
- **THEN** the system SHALL update the channel record
- **AND** return success result

#### Scenario: Delete payment channel

- **WHEN** admin sends DELETE /payment-channel/{id}
- **AND** the channel exists
- **THEN** the system SHALL mark the channel as deleted (soft delete)

#### Scenario: Paginated list query

- **WHEN** admin sends GET /payment-channel/page?pageNum=1&pageSize=10
- **THEN** the system SHALL return paginated results
- **AND** include total count

### Requirement: Payment Channel Enumeration (渠道枚举)

The system SHALL support exactly two payment channel types: UNIONPAY (银联), NETBANK (网商).

#### Scenario: Channel code stored in database

- **WHEN** admin creates a payment channel
- **THEN** the channelCode SHALL be one of: UNIONPAY, NETBANK
- **AND** the channelName SHALL be one of: 银联, 网商

### Requirement: Payment Method Enumeration (支付方式枚举)

The system SHALL support exactly five payment method types: WECHAT (微信支付), ALIPAY (支付宝), BANK_CARD (银行卡), QUICK_PASS (云闪付), POS (POS机).

#### Scenario: Payment method stored as comma-separated codes

- **WHEN** admin creates a payment channel with multiple payment methods
- **THEN** the paymentMethod SHALL be stored as comma-separated codes, e.g., "WECHAT,ALIPAY"
- **AND** each code SHALL be one of: WECHAT, ALIPAY, BANK_CARD, QUICK_PASS, POS

### Requirement: Payment Channel Fee Configuration

The system SHALL allow configuration of four fee fields: cloudAccountFee (云账户管理费率), upstreamCostFee (上游成本费率), totalFeeCost (总费率成本), perTransactionLimit (单笔限额).

#### Scenario: Configure all fee fields

- **WHEN** admin creates a payment channel with all fee fields populated
- **THEN** the system SHALL store all four fee values
- **AND** return them in subsequent queries

### Requirement: Payment Channel Status Management

The system SHALL support enabling and disabling payment channels through the status field.

#### Scenario: Disable payment channel

- **WHEN** admin sends PUT /payment-channel/{id} with status=0
- **THEN** the channel SHALL be marked as disabled
- **AND** still be visible in list queries with status=0

### Requirement: Platform-Level Configuration

The system SHALL store payment channels as platform-level configuration accessible to all tenants.

#### Scenario: Channel visible to all tenants

- **WHEN** any tenant queries payment channel list
- **THEN** the system SHALL return the same channel configuration
- **AND** the channel SHALL NOT be filtered by tenantId

## ADDED Requirements

### Requirement: System Parameter CRUD

The system SHALL provide CRUD operations for system parameters.

#### Scenario: Create system parameter with valid data

- **WHEN** admin sends `POST /system-parameter` with valid paramKey, paramName, paramValue, paramType
- **THEN** the system SHALL create the parameter and return the created parameter id

#### Scenario: Create parameter with duplicate key

- **WHEN** admin sends `POST /system-parameter` with a paramKey that already exists
- **THEN** the system SHALL return error "参数编码已存在"

#### Scenario: Update system parameter

- **WHEN** admin sends `PUT /system-parameter/{id}` with valid data
- **THEN** the system SHALL update the parameter and return success

#### Scenario: Update parameter with duplicate key

- **WHEN** admin sends `PUT /system-parameter/{id}` with a paramKey that belongs to another parameter
- **THEN** the system SHALL return error "参数编码已存在"

#### Scenario: Delete system parameter

- **WHEN** admin sends `DELETE /system-parameter/{id}`
- **THEN** the system SHALL soft-delete the parameter

#### Scenario: Get parameter by id

- **WHEN** admin sends `GET /system-parameter/{id}`
- **THEN** the system SHALL return the parameter details

#### Scenario: Get parameter by key

- **WHEN** admin sends `GET /system-parameter/key/{key}`
- **THEN** the system SHALL return the parameter with the specified key

#### Scenario: Page query parameters

- **WHEN** admin sends `GET /system-parameter/page?pageNum=1&pageSize=10`
- **THEN** the system SHALL return paginated parameter list
- **AND** support filtering by paramName (like) and status (eq)

#### Scenario: List all parameters

- **WHEN** admin sends `GET /system-parameter/list`
- **THEN** the system SHALL return all active parameters ordered by createTime desc

### Requirement: Parameter Type Validation

The system SHALL validate parameter values against their declared types.

#### Scenario: Validate INTEGER type parameter

- **WHEN** a parameter with paramType=INTEGER is created with paramValue="123abc"
- **THEN** the system SHALL return error "参数值格式不正确"

#### Scenario: Validate BOOLEAN type parameter

- **WHEN** a parameter with paramType=BOOLEAN is created with paramValue="maybe"
- **THEN** the system SHALL return error "参数值格式不正确"

#### Scenario: Validate DECIMAL type parameter

- **WHEN** a parameter with paramType=DECIMAL is created with paramValue="12.34.56"
- **THEN** the system SHALL return error "参数值格式不正确"

### Requirement: Platform-level Data

System parameters are platform-level data and SHALL NOT be isolated by tenant.

- **WHEN** any tenant queries system parameters
- **THEN** the system SHALL return the same global parameters
- **AND** the parameter tables SHALL use BaseEntity (not TenantEntity)

## ADDED Requirements

### Requirement: Data Dictionary CRUD

The system SHALL provide CRUD operations for data dictionary items.

#### Scenario: Create dictionary item with valid data

- **WHEN** admin sends `POST /data-dictionary` with valid dictCode, dictType, dictLabel, dictValue
- **THEN** the system SHALL create the dictionary item and return the created item id

#### Scenario: Create dictionary with duplicate code

- **WHEN** admin sends `POST /data-dictionary` with a dictCode that already exists
- **THEN** the system SHALL return error "字典编码已存在"

#### Scenario: Update dictionary item

- **WHEN** admin sends `PUT /data-dictionary/{id}` with valid data
- **THEN** the system SHALL update the dictionary item and return success

#### Scenario: Delete dictionary item

- **WHEN** admin sends `DELETE /data-dictionary/{id}`
- **THEN** the system SHALL soft-delete the dictionary item

#### Scenario: Get dictionary by id

- **WHEN** admin sends `GET /data-dictionary/{id}`
- **THEN** the system SHALL return the dictionary item details

#### Scenario: Page query dictionaries

- **WHEN** admin sends `GET /data-dictionary/page?pageNum=1&pageSize=10`
- **THEN** the system SHALL return paginated dictionary list
- **AND** support filtering by dictType (eq) and status (eq)

#### Scenario: List dictionaries by type

- **WHEN** admin sends `GET /data-dictionary/type/{type}`
- **THEN** the system SHALL return all active dictionary items with the specified type
- **AND** ordered by sort asc, createTime desc

#### Scenario: Get all dictionary type groups

- **WHEN** admin sends `GET /data-dictionary/all-group`
- **THEN** the system SHALL return all distinct dictType values
- **AND** each type includes its active dictionary items

#### Scenario: List all dictionaries

- **WHEN** admin sends `GET /data-dictionary/list`
- **THEN** the system SHALL return all active dictionary items ordered by dictType, sort

### Requirement: Unique Dictionary Code

Dictionary codes SHALL be globally unique across all types.

- **WHEN** a dictionary item with dictCode="status_active" is created under type "payment_status"
- **AND** another dictionary item with dictCode="status_active" is created under type "order_status"
- **THEN** the second creation SHALL succeed (same code, different types is allowed)
- **OR** if the system requires global uniqueness, return error "字典编码已存在"

### Requirement: Platform-level Data

Data dictionaries are platform-level data and SHALL NOT be isolated by tenant.

- **WHEN** any tenant queries data dictionaries
- **THEN** the system SHALL return the same global dictionary items
- **AND** the dictionary tables SHALL use BaseEntity (not TenantEntity)

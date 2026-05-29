## ADDED Requirements

### Requirement: Entity Inheritance Hierarchy

All database entities SHALL extend one of the two base classes:

| Base Class | Usage | Automatic Tenant Isolation |
|------------|-------|--------------------------|
| `BaseEntity` | Platform-level tables (menu, role) | No |
| `TenantEntity` | Tenant-specific tables (user, order) | Yes |

#### Scenario: Platform-level entity

- **WHEN** a developer creates an entity for a table that is shared across all tenants (e.g., `saas_auth_menu`)
- **THEN** the entity SHALL extend `BaseEntity` (not `TenantEntity`)
- **AND** the table SHALL NOT have a `tenant_id` column

#### Scenario: Tenant-specific entity

- **WHEN** a developer creates an entity for a table that is tenant-isolated (e.g., `saas_auth_user`)
- **THEN** the entity SHALL extend `TenantEntity`
- **AND** the table SHALL have a `tenant_id` column populated by `MetaObjectHandler`

### Requirement: Table Name Mapping

Entities SHALL use `@TableName` to explicitly map to the database table name.

#### Scenario: Explicit table mapping

- **WHEN** an entity class is created
- **THEN** it SHALL have `@TableName("saas_auth_user")` annotation
- **AND** the table name SHALL match the actual database table name exactly

### Requirement: ID Generation Strategy

All entities SHALL use `@TableId` with `IdType.ASSIGN_ID` (snowflake algorithm) for primary key generation.

#### Scenario: Primary key annotation

- **WHEN** an entity has an `id` field
- **THEN** it SHALL be annotated with `@TableId(value = "id", type = IdType.ASSIGN_ID)`

### Requirement: Logical Deletion

Entities SHALL use `@TableLogic` annotation on the `deleted` field for soft deletion.

#### Scenario: Soft delete field

- **WHEN** an entity has a `deleted` column for soft deletion
- **THEN** the field SHALL be annotated with `@TableLogic`
- **AND** MyBatis-Plus queries SHALL automatically filter `deleted = 0`

### Requirement: Field Auto-fill

Timestamp fields (`createTime`, `updateTime`) and user fields (`createBy`, `updateBy`) SHALL use `@TableField(fill = FieldFill.INSERT)` or `FieldFill.INSERT_UPDATE`.

#### Scenario: Create timestamp field

- **WHEN** an entity has a `createTime` field
- **THEN** it SHALL be annotated with `@TableField(fill = FieldFill.INSERT)`

#### Scenario: Update timestamp field

- **WHEN** an entity has an `updateTime` field
- **THEN** it SHALL be annotated with `@TableField(fill = FieldFill.INSERT_UPDATE)`

### Requirement: Entity Location

Entities SHALL be placed in the `{module}/entity/` package and SHALL NOT be placed in `common` modules.

#### Scenario: Entity package location

- **WHEN** a developer creates an entity for the auth module
- **THEN** the entity SHALL be in `com.lzlj.account.user.entity` (not in `common-core`)
- **AND** it SHALL NOT be shared across modules

### Requirement: Log Table Exception

Tables that have their own `tenantId` field (e.g., `saas_auth_operation_log`) SHALL extend `BaseEntity` (not `TenantEntity`) to avoid double tenant filtering.

#### Scenario: Operation log entity

- **WHEN** an entity represents an operation log table
- **THEN** the entity SHALL extend `BaseEntity`
- **AND** the entity SHALL have its own `tenantId` field with `@TableField(fill = FieldFill.INSERT)`

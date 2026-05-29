## ADDED Requirements

### Requirement: DTO Naming Convention

All DTOs SHALL follow the naming convention based on their purpose:

| DTO Type | Suffix | Usage |
|-----------|--------|-------|
| Create Request | `CreateXxxDTO` | POST requests to create resources |
| Update Request | `UpdateXxxDTO` | PUT/PATCH requests to modify resources |
| Response | `XxxDTO` | API response body |
| Page Request | `PageRequest` | Pagination query parameters |
| Page Response | `PageResult<T>` | Paginated list response |

#### Scenario: Create user DTO

- **WHEN** a developer creates a DTO for creating a user
- **THEN** it SHALL be named `CreateUserDTO`
- **AND** it SHALL contain only creation-relevant fields (username, password, realName, etc.)

#### Scenario: Update user DTO

- **WHEN** a developer creates a DTO for updating a user
- **THEN** it SHALL be named `UpdateUserDTO`
- **AND** it SHALL contain updatable fields (realName, phone, email, etc.)
- **AND** it SHALL NOT contain auto-generated fields (id, createTime, etc.)

### Requirement: DTO Field Validation

DTO fields SHALL use Jakarta Validation annotations (`@NotNull`, `@NotBlank`, `@Size`, etc.) for input validation.

#### Scenario: Required field validation

- **WHEN** a CreateUserDTO has a required `username` field
- **THEN** it SHALL be annotated with `@NotBlank(message = "用户名不能为空")`

#### Scenario: Optional field with default

- **WHEN** a DTO field is optional
- **THEN** it SHALL NOT have `@NotNull` or `@NotBlank`
- **AND** a default value SHALL be handled in the Service layer

### Requirement: DTO Location

DTOs SHALL be placed in the `{module}/dto/` package and SHALL NOT use Entity classes directly as request DTOs.

#### Scenario: DTO package location

- **WHEN** a developer creates a DTO for the user module
- **THEN** it SHALL be in `com.lzlj.account.user.dto`
- **AND** Controller methods SHALL accept DTO (not Entity) for request bodies

#### Scenario: DTO vs Entity separation

- **WHEN** a developer creates a Controller method
- **THEN** the request body parameter SHALL be a DTO (e.g., `CreateUserDTO`)
- **AND** it SHALL NOT directly accept an Entity as the request parameter

### Requirement: Response DTO Serialization

All DTOs SHALL implement `Serializable` for cross-service transmission.

#### Scenario: Serializable DTO

- **WHEN** a developer creates a response DTO that may be used in Feign calls
- **THEN** the DTO class SHALL implement `Serializable`

### Requirement: Schema Documentation

DTOs SHALL use `@Schema(description = "...")` annotations for Swagger/OpenAPI documentation.

#### Scenario: DTO field documentation

- **WHEN** a developer creates a DTO field
- **THEN** each field SHALL have `@Schema(description = "...")` annotation
- **AND** the description SHALL be in Chinese matching the business meaning

### Requirement: No VO Separation

The project SHALL NOT use VO (Value Object) as a separate type. All data transfer objects SHALL use the DTO naming convention.

#### Scenario: Single DTO type for create and response

- **WHEN** a developer needs a type for both creating and returning user data
- **THEN** they SHALL use `CreateUserDTO` (for create) and `UserDTO` (for response)
- **AND** they SHALL NOT create a separate `UserVO`

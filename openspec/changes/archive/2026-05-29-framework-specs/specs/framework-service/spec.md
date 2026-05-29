## ADDED Requirements

### Requirement: Service Layer Dependency Injection

Services SHALL use constructor injection via `@RequiredArgsConstructor` (Lombok). Field injection via `@Autowired` SHALL NOT be used.

#### Scenario: Constructor injection

- **WHEN** a developer creates a Service implementation
- **THEN** dependencies SHALL be injected via `private final XxxDao xxxDao` constructor parameter
- **AND** the class SHALL be annotated with `@RequiredArgsConstructor`
- **AND** `@Autowired` SHALL NOT be used on fields

### Requirement: Transaction Boundaries

`@Transactional(rollbackFor = Exception.class)` SHALL be used for operations that modify multiple tables or require atomicity. Single-table CRUD operations do not need explicit transaction.

#### Scenario: Multi-table transaction

- **WHEN** a service method performs operations on multiple tables (e.g., create user + assign roles)
- **THEN** the method SHALL be annotated with `@Transactional(rollbackFor = Exception.class)`

#### Scenario: Single-table create

- **WHEN** a service method only inserts into a single table
- **THEN** `@Transactional` is optional (MyBatis-Plus auto-transaction)

### Requirement: Business Exception Usage

Business errors SHALL throw `BusinessException` (not `RuntimeException` or generic `Exception`).

#### Scenario: Resource not found

- **WHEN** a service cannot find a requested resource
- **THEN** it SHALL throw `throw new BusinessException(ResultCode.DATA_NOT_FOUND)`
- **AND** it SHALL NOT return null

#### Scenario: Custom error message

- **WHEN** a business rule is violated
- **THEN** it SHALL throw `throw new BusinessException(ResultCode.FAIL.getCode(), "自定义错误信息")`

### Requirement: Service Method Return Types

Service methods SHALL return DTOs (not Entities) to the Controller layer.

#### Scenario: Entity to DTO conversion

- **WHEN** a service method retrieves data from the database
- **THEN** it SHALL return a DTO (e.g., `UserDTO`)
- **AND** it SHALL use `BeanCopyUtils.copy()` or similar to convert Entity to DTO

### Requirement: @Async and ThreadLocal

When using `@Async`, ThreadLocal values (userId, tenantId) MUST be captured BEFORE the async call and passed as parameters. Accessing ThreadLocal in async context SHALL NOT be done.

#### Scenario: Async operation with tenant context

- **WHEN** a service method needs to perform an async operation
- **THEN** the developer SHALL capture `TenantContext.getTenantId()` BEFORE calling the async method
- **AND** the tenantId SHALL be passed as a parameter to the async method
- **AND** the developer SHALL NOT rely on ThreadLocal inside the async execution

### Requirement: No Business Logic in Controllers

Controllers SHALL only handle request/response mapping and delegate all business logic to the Service layer.

#### Scenario: Controller delegating to service

- **WHEN** a Controller receives a create request
- **THEN** it SHALL call `xxxService.create(dto)` and return the result
- **AND** it SHALL NOT contain business logic (validation, database operations, etc.)

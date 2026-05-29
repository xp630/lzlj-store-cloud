## ADDED Requirements

### Requirement: FeignClient Location

FeignClient interfaces SHALL be placed in API modules (`cloud-account-common-api` or `{module}-api`) and SHALL NOT be in biz modules.

#### Scenario: FeignClient placement

- **WHEN** a developer creates a FeignClient for user service
- **THEN** it SHALL be placed in `cloud-account-common-api/feign/UserFeignClient.java`
- **AND** it SHALL be accessible to all modules that need to call the user service

### Requirement: FeignClient Annotation

FeignClients SHALL use the following annotation structure:

```java
@FeignClient(
    name = "service-name",    // Nacos registered service name
    path = "/xxx",            // Controller base path
    fallback = XxxFallback.class  // Optional fallback
)
```

#### Scenario: Standard FeignClient

- **WHEN** a developer creates a FeignClient for the auth service
- **THEN** it SHALL use `@FeignClient(name = "saas-auth", path = "/user")`

### Requirement: Return Type

FeignClient methods SHALL return `Result<T>` to match the Controller return type convention.

#### Scenario: Return type matching

- **WHEN** a developer defines a FeignClient method
- **THEN** the return type SHALL be `Result<XxxDTO>` (not `XxxDTO` directly)

### Requirement: Path Variable Mapping

FeignClient methods with path variables SHALL use `@PathVariable` with explicit name:

```java
@GetMapping("/{id}")
Result<UserDTO> getById(@PathVariable("id") Long id);
```

#### Scenario: Path variable with explicit name

- **WHEN** a FeignClient method has a path variable
- **THEN** `@PathVariable("id")` SHALL have the explicit name specified

### Requirement: Fallback Implementation

FeignClients SHOULD have a Fallback class for resilience. The Fallback SHALL return a failure Result with an appropriate message.

#### Scenario: Fallback implementation

- **WHEN** a FeignClient has a fallback
- **THEN** the fallback class SHALL implement the interface
- **AND** each method SHALL return `Result.fail("服务暂时不可用")`

### Requirement: Context Propagation

Feign interceptors SHALL propagate tenant context headers (`X-Tenant-Id`, `X-User-Id`) to downstream services.

#### Scenario: Tenant context in Feign call

- **WHEN** a service makes a Feign call to another service
- **THEN** the `TenantContextFeignInterceptor` SHALL copy `X-Tenant-Id` header from the incoming request
- **AND** the downstream service SHALL receive the correct tenant context

### Requirement: Timeout Configuration

FeignClients SHALL have reasonable timeout configurations to prevent hanging requests.

#### Scenario: Timeout configuration

- **WHEN** a FeignClient is configured
- **THEN** it SHOULD have `options` configured with `connectTimeout` and `readTimeout`
- **AND** reasonable values are 5000ms for connect and 10000ms for read

### Requirement: No Circular Dependencies

Modules SHALL NOT have circular Feign dependencies (A calls B, B calls A).

#### Scenario: Dependency violation detection

- **WHEN** module A has a FeignClient for module B
- **THEN** module B SHALL NOT have a FeignClient for module A
- **AND** if cross-calling is needed, the call SHALL be designed with a common API module

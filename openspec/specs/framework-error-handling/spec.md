## ADDED Requirements

### Requirement: Result Code Hierarchy

`ResultCode` enum SHALL define standardized codes across the system:

| Code Range | Category | Example |
|------------|----------|---------|
| 200 | Success | SUCCESS |
| 400 | Client Error | FAIL |
| 401 | Unauthorized | UNAUTHORIZED |
| 403 | Forbidden | FORBIDDEN |
| 404 | Not Found | NOT_FOUND |
| 1001-1999 | Business Error | PARAM_ERROR, DATA_NOT_FOUND, DATA_ALREADY_EXISTS |
| 2001-2999 | Auth Error | TOKEN_INVALID, TOKEN_EXPIRED |
| 3001-3999 | Permission Error | PERMISSION_DENIED, NO_PERMISSION |

#### Scenario: Business error code

- **WHEN** a service throws a business exception for invalid parameters
- **THEN** it SHALL use `ResultCode.PARAM_ERROR` (code 1001)

#### Scenario: Not found error

- **WHEN** a service cannot find a resource
- **THEN** it SHALL throw `new BusinessException(ResultCode.DATA_NOT_FOUND)`

### Requirement: Global Exception Handler

`GlobalExceptionHandler` SHALL handle the following exception types and return appropriate `Result`:

| Exception Type | HTTP Status | Result Code |
|---------------|------------|-------------|
| `BusinessException` | 200 (business error) | from exception |
| `AuthException` | 200 (auth error) | from exception |
| `MethodArgumentNotValidException` | 200 | PARAM_ERROR |
| `ConstraintViolationException` | 200 | PARAM_ERROR |
| `MissingServletRequestParameterException` | 200 | PARAM_ERROR |
| `NoHandlerFoundException` | 404 | NOT_FOUND |
| `Exception` | 200 | FAIL |

### Requirement: Validation Error Messages

Validation exception messages SHALL be extracted from the exception and returned in the response.

#### Scenario: Validation failure

- **WHEN** `@Valid` fails on a request body
- **THEN** the response message SHALL contain the specific field error (e.g., "用户名不能为空")
- **AND** the error code SHALL be `ResultCode.PARAM_ERROR`

### Requirement: No Information Leakage

Error responses SHALL NOT expose internal implementation details (stack traces, SQL errors, file paths).

#### Scenario: Internal error handling

- **WHEN** an unexpected exception occurs
- **THEN** the error message to the client SHALL be generic ("系统错误")
- **AND** detailed error information SHALL be logged server-side only

### Requirement: BusinessException Constructor Usage

`BusinessException` SHALL be thrown with either:
1. `new BusinessException(ResultCode.XXX)` — using standard code
2. `new BusinessException(code, message)` — with custom message

#### Scenario: Standard business exception

- **WHEN** a resource is not found
- **THEN** throw `new BusinessException(ResultCode.DATA_NOT_FOUND)`

#### Scenario: Custom message

- **WHEN** a business rule violation needs a specific message
- **THEN** throw `new BusinessException(ResultCode.FAIL.getCode(), "用户名已存在")`

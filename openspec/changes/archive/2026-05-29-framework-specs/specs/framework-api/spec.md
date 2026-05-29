## ADDED Requirements

### Requirement: REST API Path Conventions

API paths SHALL follow REST conventions using lowercase, hyphen-separated nouns:

| Pattern | Example | Usage |
|---------|---------|-------|
| `/xxx` | `/user` | Resource collection |
| `/{id}` | `/user/{id}` | Specific resource |
| `/xxx/page` | `/user/page` | Paginated list |

#### Scenario: Resource naming

- **WHEN** a developer defines an API path for users
- **THEN** the path SHALL be `/user` (not `/users`, `/User`, `/userList`)

### Requirement: HTTP Method Selection

CRUD operations SHALL use the correct HTTP method:

| Operation | Method | Example |
|-----------|--------|---------|
| Create | POST | `POST /user` |
| Read | GET | `GET /user/{id}` |
| List/Page | GET | `GET /user/page` |
| Update | PUT | `PUT /user/{id}` |
| Delete | DELETE | `DELETE /user/{id}` |

### Requirement: API Versioning

APIs SHALL use path versioning for major version changes: `/api/v1/xxx`.

#### Scenario: Versioned API

- **WHEN** a breaking change is introduced to an API
- **THEN** the new version SHALL be at `/api/v2/xxx`
- **AND** the old version SHALL be maintained for backward compatibility

### Requirement: Pagination API

Paginated endpoints SHALL accept query parameters `pageNum` and `pageSize`, and return `PageResult<T>`.

#### Scenario: Pagination request parameters

- **WHEN** a developer creates a paginated endpoint
- **THEN** the method SHALL accept `pageNum` and `pageSize` as `@RequestParam`
- **AND** the return type SHALL be `Result<PageResult<XxxDTO>>`

### Requirement: Consistent Response Format

All APIs SHALL return `Result<T>` wrapper. Successful responses SHALL use `Result.success()`.

#### Scenario: Success response structure

- **WHEN** an API returns successfully with data
- **THEN** the response body SHALL be `{"code": 200, "message": "success", "data": {...}}`

#### Scenario: Error response structure

- **WHEN** an API returns an error
- **THEN** the response body SHALL be `{"code": <error_code>, "message": "<error_message>", "data": null}`

### Requirement: OpenAPI Documentation

All endpoints SHALL have `@Operation(summary = "...")` annotation with a concise description of the action.

#### Scenario: Endpoint documentation

- **WHEN** a developer creates an API endpoint
- **THEN** the method SHALL have `@Operation(summary = "获取用户详情")` annotation
- **AND** the description SHALL be in Chinese, starting with an action verb

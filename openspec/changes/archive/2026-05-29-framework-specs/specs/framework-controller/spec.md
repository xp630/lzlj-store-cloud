## ADDED Requirements

### Requirement: Controller Class Annotations

Every REST Controller SHALL use the following annotations consistently:
- `@Tag(name = "...")` for Swagger group name
- `@RestController` (not `@Controller`)
- `@RequestMapping("/xxx")` at class level for path prefix
- `@RequiredArgsConstructor` for constructor injection

#### Scenario: Standard Controller Structure

- **WHEN** a developer creates a new REST Controller
- **THEN** the class SHALL be annotated with `@Tag`, `@RestController`, `@RequestMapping`, and `@RequiredArgsConstructor`
- **AND** the `@RequestMapping` path SHALL follow REST conventions (e.g., `/user`, `/menu`, `/role`)

### Requirement: Method-level Annotations

Every API endpoint SHALL use:
- `@Operation(summary = "...")` for Swagger description
- HTTP method annotation (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`)
- `@Valid` on `@RequestBody` parameters for DTO validation

#### Scenario: POST endpoint with validation

- **WHEN** a developer creates a POST endpoint that accepts a DTO body
- **THEN** the DTO parameter SHALL be annotated with `@Valid @RequestBody`
- **AND** the method SHALL have `@Operation(summary = "...")` describing the action

#### Scenario: GET endpoint with path variable

- **WHEN** a developer creates a GET endpoint with a path variable
- **THEN** the method SHALL use `@GetMapping("/{id}")` with `@PathVariable("id")`

### Requirement: Return Type

All Controller methods SHALL return `Result<T>` (not raw objects or `void`).

#### Scenario: Success response

- **WHEN** an endpoint successfully returns data
- **THEN** it SHALL return `Result.success(data)` or `Result.success()`
- **AND** it SHALL NOT return raw objects directly

#### Scenario: Void endpoint

- **WHEN** an endpoint performs an action without returning data (e.g., DELETE)
- **THEN** it SHALL return `Result.success()` (not `void`)

### Requirement: Parameter Source Annotations

Parameters SHALL use correct source annotations:
- `@RequestBody` for JSON body (DTO)
- `@PathVariable` for path segments
- `@RequestParam` for query parameters
- `@RequestHeader` for HTTP headers

#### Scenario: Pagination request

- **WHEN** an endpoint accepts pagination parameters
- **THEN** query parameters SHALL use `@RequestParam` (e.g., `@RequestParam("pageNum") Integer pageNum`)

### Requirement: Operation Logging

Sensitive or significant operations SHALL be annotated with `@OperationLog(module = "...", operation = "...", content = "...")`.

#### Scenario: Create operation logging

- **WHEN** a developer creates an endpoint that creates a resource
- **THEN** the method SHALL have `@OperationLog(module = "menu", operation = "CREATE", content = "创建菜单")` annotation

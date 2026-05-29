## ADDED Requirements

### Requirement: Avatar Upload Endpoint

The system SHALL provide an avatar upload API that allows authenticated users to upload their profile picture using the OSS presigned URL mechanism.

#### Scenario: Successful avatar upload

- **WHEN** an authenticated user calls `GET /user/avatar/presigned-url?filename=photo.jpg&size=102400`
- **AND** the user has `userId=123` and `tenantId=1` from JWT token
- **THEN** the system SHALL return a presigned URL with `fileUrl` matching `avatar/123/{uuid}.jpg`

#### Scenario: Unauthenticated avatar upload request

- **WHEN** a request is made to `/user/avatar/presigned-url` without valid authentication
- **THEN** the system SHALL return `Result.fail(ResultCode.UNAUTHORIZED)`

### Requirement: Avatar Upload Callback

After uploading to OSS, the client SHALL notify the server with the final file path so the system can update the user's avatar field.

#### Scenario: Update user avatar after successful OSS upload

- **WHEN** a user calls `POST /user/avatar` with body `{"avatar": "avatar/123/550e8400.jpg"}`
- **THEN** the system SHALL update the user's `avatar` field in database
- **AND** return `Result.success()`

#### Scenario: Update avatar for non-existent user

- **WHEN** a user calls `POST /user/avatar` but the user record does not exist
- **THEN** the system SHALL return `Result.fail(ResultCode.DATA_NOT_FOUND)`

### Requirement: Avatar Field in User Entity

The `saas_sys_user` table SHALL have an `avatar` column (VARCHAR 255) to store the avatar URL. This field already exists in the current schema.

#### Scenario: Get user with avatar

- **WHEN** a client requests user info via `/user/{id}` or `/user/current`
- **THEN** the returned `UserDTO` SHALL include the `avatar` field
- **AND** the avatar URL SHALL be the value stored in the database

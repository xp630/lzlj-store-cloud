## ADDED Requirements

### Requirement: OSS Optional Configuration

The system SHALL support optional OSS integration. When `oss.enabled=true` is configured in Nacos, the OSS upload capability SHALL be available. When not configured, the system SHALL start normally but upload operations SHALL return an error response.

#### Scenario: OSS not configured - system startup

- **WHEN** the application starts without `oss.enabled=true` in Nacos configuration
- **THEN** the application SHALL start successfully without errors
- **AND** the `OssUploadService` bean SHALL NOT be created

#### Scenario: OSS not configured - upload attempt

- **WHEN** a client calls the upload API without OSS configured
- **THEN** the system SHALL return `Result.fail("OSS未配置")`
- **AND** the system SHALL NOT throw an exception to the client

### Requirement: Presigned URL Generation

The system SHALL provide a presigned URL generation endpoint that allows clients to upload files directly to OSS without passing through the application server.

#### Scenario: Generate presigned URL with valid parameters

- **WHEN** a client sends `GET /upload/presigned-url?filename=avatar.jpg&contentType=image/jpeg&size=102400`
- **AND** `oss.enabled=true` is configured
- **THEN** the system SHALL return a valid presigned upload URL with 5-minute expiration
- **AND** the response SHALL include both `uploadUrl` (OSS endpoint) and `fileUrl` (CDN or OSS public URL)

#### Scenario: Generate presigned URL with unsupported content type

- **WHEN** a client sends `GET /upload/presigned-url?filename=evil.exe&contentType=application/octet-stream`
- **THEN** the system SHALL return `Result.fail("不支持的文件类型")`
- **AND** no presigned URL SHALL be generated

#### Scenario: Generate presigned URL with file size exceeding limit

- **WHEN** a client sends `GET /upload/presigned-url?filename=big.jpg&contentType=image/jpeg&size=10485760` (10MB)
- **THEN** the system SHALL return `Result.fail("文件大小超出限制")`

### Requirement: Content Type Whitelist

The system SHALL only accept the following content types for upload:
- `image/jpeg`
- `image/png`
- `image/gif`
- `image/webp`

#### Scenario: Request with valid image content type

- **WHEN** a client requests presigned URL with `contentType=image/png`
- **THEN** the system SHALL accept the request and generate a valid presigned URL

#### Scenario: Request with invalid content type

- **WHEN** a client requests presigned URL with `contentType=application/pdf`
- **THEN** the system SHALL reject the request with error code

### Requirement: File Path Generation

The system SHALL generate deterministic file paths following the pattern `{type}/{userId}/{uuid}.{ext}`, where `ext` is extracted from the original filename.

#### Scenario: Avatar file path generation

- **WHEN** a client requests presigned URL for `filename=avatar.jpg` with `type=avatar` and `userId=123`
- **THEN** the generated `fileUrl` SHALL match pattern `avatar/123/{uuid}.jpg`
- **AND** the `uuid` SHALL be a randomly generated UUID v4

### Requirement: CDN Domain Support

The system SHALL support an optional CDN domain for returning public file URLs. When `oss.cdn-domain` is configured, the returned `fileUrl` SHALL use the CDN domain instead of the raw OSS endpoint.

#### Scenario: CDN domain configured

- **WHEN** `oss.cdn-domain=https://cdn.example.com` is configured
- **AND** a client requests presigned URL for `avatar.jpg`
- **THEN** the returned `fileUrl` SHALL be `https://cdn.example.com/avatar/123/uuid.jpg`

#### Scenario: CDN domain not configured

- **WHEN** `oss.cdn-domain` is not configured
- **AND** a client requests presigned URL
- **THEN** the returned `fileUrl` SHALL use the raw OSS public URL

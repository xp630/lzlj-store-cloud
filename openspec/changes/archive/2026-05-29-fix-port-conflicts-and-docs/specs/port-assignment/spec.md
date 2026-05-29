## ADDED Requirements

### Requirement: Service Port Assignment

The project SHALL define and maintain a canonical port assignment for all services to prevent port conflicts during local development and deployment.

#### Scenario: Port conflict detection during development

- **WHEN** a developer attempts to start two services configured on the same port
- **THEN** the second service startup SHALL fail with a clear port binding error
- **AND** the affected services SHALL be documented with non-conflicting ports

#### Scenario: Canonical port allocation

The following port allocation SHALL be maintained in all environment configuration files:

| Service | Port | Service Name (Nacos) | Module |
|---------|------|---------------------|--------|
| saas-auth | 9092 | saas-auth | cloud-account-saas-biz-auth |
| account-lzlj-user | 9093 | account-lzlj-user | cloud-account-lzlj-biz-user |
| lzlj-auth | 9294 | lzlj-auth | cloud-account-lzlj-biz-auth |
| account-gateway (SaaS) | 18080 | account-gateway | cloud-account-saas-gateway |
| account-gateway-lzlj | 28080 | account-gateway-lzlj | cloud-account-lzlj-gateway |

#### Scenario: New service port assignment

- **WHEN** a new service is introduced to the project
- **THEN** it SHALL be assigned an unused port from the range 9095-65535
- **AND** the port assignment SHALL be documented in README.md and this spec

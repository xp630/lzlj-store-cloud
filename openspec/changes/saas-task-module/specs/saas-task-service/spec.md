# SaaS Task Service Specification

## ADDED Requirements

### Requirement: saas-task-service serves as centralized task scheduler

The system SHALL provide an independent task scheduling service that:
- Runs as a separate deployment unit (`saas-biz-task`)
- Integrates with `cloud-account-common-schedule` xxl-job adapter
- Executes tasks by calling business service interfaces via Feign
- Does NOT contain any business logic

#### Scenario: Task service starts successfully
- **WHEN** `SaasTaskApplication` starts
- **THEN** it registers all `ScheduleTask` implementations with `ScheduleTaskRegistry`
- **AND** xxl-job executor is initialized

#### Scenario: Task executes via Feign call
- **WHEN** xxl-job triggers `scheduleTaskExecutor` with task name `menu-refresh`
- **THEN** `SaasCacheRefreshTask.execute()` is called
- **AND** it makes HTTP call via `SaasCacheFeignClient.refreshMenus()`
- **AND** returns execution result

---

### Requirement: Module transformation removes goods code

The system SHALL transform `saas-biz-task` module from goods service to task service:
- All `goods/*` packages SHALL be removed
- Module artifact SHALL be renamed to `cloud-account-saas-task`
- Module SHALL exclude database auto-configuration
- Module SHALL include `cloud-account-common-schedule` dependency

#### Scenario: Goods code is removed
- **WHEN** transformation is complete
- **THEN** no classes under `com.lzlj.account.goods` package exist
- **AND** `SaasGoodsApplication.java` is replaced with `SaasTaskApplication.java`

---

### Requirement: Internal API exposes cache refresh endpoints

The system SHALL provide internal HTTP endpoints for cache refresh operations:
- Endpoint path prefix: `/internal/`
- No authentication required for internal calls
- Interfaces are reserved for task service invocation

#### Scenario: Refresh menus cache
- **WHEN** `POST /internal/cache/menus/refresh` is called
- **THEN** `SaasCacheService.invalidateMenus()` is invoked
- **AND** all menu caches are invalidated

#### Scenario: Refresh roles cache
- **WHEN** `POST /internal/cache/roles/refresh` is called
- **THEN** `SaasCacheService.invalidateRoles()` is invoked
- **AND** all role caches are invalidated

#### Scenario: Refresh data dictionary cache
- **WHEN** `POST /internal/cache/dict/{dictType}/refresh` is called
- **THEN** `SaasCacheService.invalidateDataDictionary(dictType)` is invoked
- **AND** specified dictionary type cache is invalidated

#### Scenario: Refresh all data dictionary cache
- **WHEN** `POST /internal/cache/dict/all/refresh` is called
- **THEN** `SaasCacheService.invalidateDataDictionary(null)` is invoked
- **AND** all dictionary caches are invalidated

---

### Requirement: Feign client calls internal API

The system SHALL provide Feign clients for task service to call:
- Feign client definitions placed in `cloud-account-saas-api-auth`
- Fallback implementations handle call failures gracefully
- Each Feign client maps to corresponding internal API endpoint

#### Scenario: Menu refresh Feign call succeeds
- **WHEN** `SaasCacheFeignClient.refreshMenus()` is invoked
- **THEN** HTTP POST is sent to `saas-auth/internal/cache/menus/refresh`
- **AND** returns success result

#### Scenario: Menu refresh Feign call fails
- **WHEN** `SaasCacheFeignClient.refreshMenus()` invocation fails
- **THEN** `SaasCacheFeignClientFallback` returns fallback result
- **AND** task execution continues with warning log

---

### Requirement: Cache refresh task registers with scheduler

The system SHALL provide `ScheduleTask` implementations for cache refresh:
- Each task implements `ScheduleTask` interface from `cloud-account-common-schedule`
- Task name uniquely identifies the operation
- Tasks make Feign calls to business service

#### Scenario: Menu refresh task executes
- **WHEN** task with name `menu-refresh` is triggered
- **THEN** `MenuCacheRefreshTask.execute()` is called
- **AND** it invokes `saasCacheFeignClient.refreshMenus()`
- **AND** returns result string

#### Scenario: Role refresh task executes
- **WHEN** task with name `role-refresh` is triggered
- **THEN** `RoleCacheRefreshTask.execute()` is called
- **AND** it invokes `saasCacheFeignClient.refreshRoles()`
- **AND** returns result string

#### Scenario: Data dictionary refresh task executes
- **WHEN** task with name `dict-refresh` is triggered
- **THEN** `DictCacheRefreshTask.execute()` is called
- **AND** it invokes `saasCacheFeignClient.refreshAllDataDictionary()`
- **AND** returns result string

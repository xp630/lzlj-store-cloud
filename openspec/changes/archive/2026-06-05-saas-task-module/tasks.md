## 1. Module Transformation - Clear Goods Code

- [x] 1.1 Delete `goods/` package and all contents under `saas-biz-task/src/main/java/com/lzlj/account/`
- [x] 1.2 Rename `SaasGoodsApplication.java` to `SaasTaskApplication.java` and rewrite content
- [x] 1.3 Update `application.yml` with task service config (port, name, etc.)
- [x] 1.4 Verify module compiles without goods code

## 2. Add Schedule Dependencies

- [x] 2.1 Add `cloud-account-common-schedule` dependency to `pom.xml`
- [x] 2.2 Add `cloud-account-saas-api-auth` dependency to `pom.xml` for Feign clients
- [x] 2.3 Add `cloud-account-common-redis` dependency for cache operations
- [x] 2.4 Configure `schedule.enabled=true` and xxl-job settings in `application.yml`

## 3. saas-auth - Create Internal Cache Controller

- [x] 3.1 Create `com.lzlj.account.cache.internal.InternalCacheController`
- [x] 3.2 Add `POST /internal/cache/menus/refresh` endpoint
- [x] 3.3 Add `POST /internal/cache/roles/refresh` endpoint
- [x] 3.4 Add `POST /internal/cache/dict/{dictType}/refresh` endpoint
- [x] 3.5 Add `POST /internal/cache/dict/all/refresh` endpoint
- [x] 3.6 Inject `SaasCacheService` and call invalidation methods

## 4. cloud-account-saas-api-auth - Create Feign Client

- [x] 4.1 Create `SaasCacheFeignClient` interface in `cloud-account-saas-api-auth` with `@FeignClient(name = "saas-auth", path = "/internal/cache")`
- [x] 4.2 Define `refreshMenus()` method with `POST /menus/refresh`
- [x] 4.3 Define `refreshRoles()` method with `POST /roles/refresh`
- [x] 4.4 Define `refreshDict(dictType)` method with `POST /dict/{dictType}/refresh`
- [x] 4.5 Define `refreshAllDict()` method with `POST /dict/all/refresh`
- [x] 4.6 Create `SaasCacheFeignClientFallback` implementing fallback logic
- [x] 4.7 Update `@FeignClient` to include `fallback = SaasCacheFeignClientFallback.class`

## 5. saas-task - Implement ScheduleTask

- [x] 5.1 Create `com.lzlj.account.task.schedule.MenuCacheRefreshTask`
- [x] 5.2 Create `com.lzlj.account.task.schedule.RoleCacheRefreshTask`
- [x] 5.3 Create `com.lzlj.account.task.schedule.DictCacheRefreshTask`
- [x] 5.4 Each task injects `SaasCacheFeignClient`
- [x] 5.5 Each task implements `name()` returning task identifier
- [x] 5.6 Each task implements `execute()` making Feign call and returning result
- [x] 5.7 Verify tasks auto-register via `ScheduleTaskRegistry` (自动通过@Component和ScheduleTaskRegistry构造函数注入实现)

## 6. Verify and Test

- [ ] 6.1 Start xxl-job admin console
- [ ] 6.2 Start `saas-task` service
- [ ] 6.3 Register tasks in xxl-job admin
- [ ] 6.4 Manually trigger `menu-refresh` task and verify cache invalidation
- [ ] 6.5 Manually trigger `role-refresh` task and verify cache invalidation
- [ ] 6.6 Manually trigger `dict-refresh` task and verify cache invalidation

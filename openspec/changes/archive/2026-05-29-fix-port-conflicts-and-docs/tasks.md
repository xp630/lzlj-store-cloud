## 1. LZLJ Auth 端口调整 (9094 → 9294)

- [x] 1.1 修改 `lzlj-auth/application-dev.yml` 端口: 9094 → 9294

## 2. LZLJ Gateway 端口调整 (18081 → 28080)

- [x] 2.1 修改 `account-gateway-lzlj/application-dev.yml` 端口: 18081 → 28080
- [x] 2.2 修改 `account-gateway-lzlj/application-prod.yml` 端口: 18081 → 28080
- [x] 2.3 修改 `account-gateway-lzlj/application.yml` 端口: 无需修改（端口在 dev/prod 中指定）

## 3. 文档端口同步更新

- [x] 3.1 更新 `README.md` 服务端口表格 (lzlj-auth: 9294, account-gateway-lzlj: 28080)
- [x] 3.2 更新 `ROADMAP.md` 快速测试命令端口 (28080, 9294)
- [x] 3.3 更新 `docs/architecture-convention.md` 端口描述 (18081→28080, 9094→9294)

## 4. Verification

- [x] 4.1 确认所有 application*.yml 中端口配置无遗漏
- [x] 4.2 确认 git diff 显示所有变更符合预期

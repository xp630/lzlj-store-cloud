# 支付模块

> 记录银联、网商接口调用和流程梳理

## 一、支付流程

```mermaid
flowchart TD
    A[老窖本地化系统] --> B[虚拟收银台<br/>线上]
    A --> C[POS终端<br/>线下]
    
    B --> D[支付宝]
    B --> E[云闪付/微信]
    C --> F[银行卡刷卡]
    
    D --> G((网商银行<br/>统一收款))
    E --> H[银联通道<br/>T+1结算]
    F --> H
    
    H --> G
    
    G --> I[补单分账]
    A --> J[清分]
    J --> K[云资金账户]
    A --> M[提现/自动提现]
    M --> K
    A --> N[冻结]
    N --> K
    K --> P[清算卡]
    I --> K
```

```mermaid
flowchart LR
    subgraph 退款流程
        A[银联退款] --> A1[平台→银联<br/>/v1/netpay/refund]
        B[网商退款] --> B1[平台→网商<br/>refund.apply]
        C[企业退款] --> C1[平台→银联<br/>transCode 202102]
    end
```

> **注意**：PC端生成的二维码已指定收款通道，如果支付方式不匹配（如支付宝扫微信支付码），会拒绝支付。

## 二、核心业务流程

### 2.1 商户入驻

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 网商

    老窖本地化系统->>平台: 发起商户入驻

    平台->>网商: 1. 图片上传 (merchant.uploadphoto)
    网商-->>平台: 图片路径

    平台->>网商: 2. 商户入驻申请 (merchant.register)<br/>RegistrationProcess=02
    网商-->>平台: 申请单号

    网商->>网商: 审核通过，发送短信激活链接

    平台->>网商: 3. 入驻结果查询 (merchant.register.query)
    网商-->>平台: 入驻状态

    网商-->>平台: 4. 入驻结果通知 (merchant.notify)
    平台-->>老窖本地化系统: 入驻结果通知

    老窖本地化系统->>平台: 发起商户信息修改
    平台->>网商: 5. 商户信息修改 (merchant.updateMerchant)<br/>含清算卡信息

    网商-->>平台: 6. 商户限权通知 (merchant.control.notify)
    平台-->>老窖本地化系统: 限权通知
```

### 2.2 支付流程

**场景A：银联收款（微信/云闪付）**

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 虚拟收银台
    participant 平台
    participant 银联
    participant 网商
    participant 云账户

    老窖本地化系统->>虚拟收银台: 唤醒收银台
    虚拟收银台->>平台: 选择支付渠道，下单
    平台->>银联: 统一下单
    银联-->>平台: 支付参数
    平台-->>虚拟收银台: 调起支付
    虚拟收银台->>平台: 支付完成
    银联-->>平台: 支付成功
    平台-->>老窖本地化系统: 支付结果通知

    平台->>网商: 补单创单 (bill.pay)
    银联->>网商: T+1结算
    平台->>网商: 上传对账文件
    平台->>网商: 创建批次 (batch.create)
    网商-->>平台: 批次完结通知

    老窖本地化系统->>平台: 调用清分
    平台->>网商: 清分到云账户
    网商-->>云账户: 资金到账
```

**场景B：POS终端收款（银行卡刷卡）**

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 银联
    participant 网商
    participant 云账户

    老窖本地化系统->>平台: 发起POS支付
    平台->>银联: 下发POS指令 (/v1/yunmis/do/action)
    银联->>银联: POS机刷卡收款
    银联-->>平台: 支付结果通知
    平台-->>老窖本地化系统: 支付结果通知

    平台->>网商: 补单创单 (bill.pay)
    银联->>网商: T+1结算
    平台->>网商: 上传对账文件
    平台->>网商: 创建批次 (batch.create)
    网商-->>平台: 批次完结通知

    老窖本地化系统->>平台: 调用清分
    平台->>网商: 清分到云账户
    网商-->>云账户: 资金到账
```

**场景C：网商收银台收款（支付宝）**

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 虚拟收银台
    participant 平台
    participant 网商
    participant 云账户

    老窖本地化系统->>虚拟收银台: 唤醒收银台
    虚拟收银台->>平台: 选择支付宝，下单
    平台->>网商: 统一创单 (unifiedorder.create)
    网商-->>平台: 支付参数
    平台-->>虚拟收银台: 调起支付宝
    虚拟收银台->>平台: 支付完成
    网商-->>平台: 支付成功
    平台-->>老窖本地化系统: 支付结果通知

    老窖本地化系统->>平台: 调用清分
    平台->>网商: 清分到云账户
    网商-->>云账户: 资金到账
```

### 2.3 退款流程

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 银联
    participant 网商

    老窖本地化系统->>平台: 发起退款

    alt 银联退款
        平台->>银联: POST /v1/netpay/refund
        银联-->>平台: 退款结果通知
    else 网商退款
        平台->>网商: refund.apply
        网商-->>平台: refund.notify
    else 企业退款
        平台->>银联: transCode 202102
        平台->>银联: transCode 202110（撤销）
    end

    平台-->>老窖本地化系统: 退款结果通知
```

### 2.4 提现流程

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 网商
    participant 云账户负责人

    老窖本地化系统->>平台: 发起提现
    平台->>网商: withdraw.apply（提现申请）
    网商->>云账户负责人: 发送短信验证码
    平台->>网商: withdraw.applyconfirm（提现确认）
    网商->>网商: 金额汇往清算卡
    网商-->>平台: withdraw.notify（提现通知）
    平台-->>老窖本地化系统: 提现结果通知
```

### 2.5 清分流程

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 网商

    老窖本地化系统->>平台: 发起清分

    alt 联动支付（自然人）
        平台->>网商: unifiedorder.create<br/>PayeeType=OUT_BANK_CARD
        网商-->>平台: 资金从平台清算专户→收方银行卡
    else 协议代扣（商户）
        平台->>网商: agreement.withhold.sign（默认签约）
        平台->>网商: order.withhold.apply（代扣）
        网商-->>平台: 资金从商户可用子户→划扣给其他商户
    end

    平台-->>老窖本地化系统: 清分结果通知
```

### 2.6 冻结/解冻

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 网商

    老窖本地化系统->>平台: 发起冻结/解冻

    alt 冻结
        平台->>网商: freeze.apply
        网商-->>平台: freeze.notify
    else 解冻
        平台->>网商: unfreeze.apply
        网商-->>平台: unfreeze.notify
    end

    平台-->>老窖本地化系统: 冻结/解冻结果通知
```

### 2.7 对账

```mermaid
sequenceDiagram
    participant 老窖本地化系统
    participant 平台
    participant 银联
    participant 网商

    老窖本地化系统->>平台: 发起对账

    alt 实时对账
        平台->>银联: POST /v1/netpay/query<br/>transCode 202106
        银联-->>平台: 对账结果
    else T+1对账
        平台->>网商: recon.query
        网商-->>平台: 对账文件
    else 日月对账单
        平台->>网商: electronicreceipt.apply
        网商-->>平台: 电子回单
    end

    平台-->>老窖本地化系统: 对账结果通知
```

## 三、支付通道路由

```mermaid
flowchart TD
    A[统一下单] --> B{payScene路由}
    
    B --> C[网商]
    B --> D[银联小程序]
    B --> E[银联H5]
    B --> F[银联APP]
    B --> G[银联扫码]
    B --> H[银联POS]
    B --> I[企业标记化]
    
    C --> C1[MYBANK_CASHIER<br/>unifiedorder.create]
    C --> C2[MYBANK_BILL<br/>bill.pay]
    
    D --> D1[UNIONPAY_WX_MINI<br/>/v1/netpay/wx/unified-order]
    D --> D2[UNIONPAY_UAC_MINI<br/>/v1/netpay/uac/mini-order]
    
    E --> E1[UNIONPAY_WX_H5<br/>/v1/netpay/h5/wx/mini-order]
    E --> E2[UNIONPAY_UAC_H5<br/>/v1/netpay/h5/uac/order]
    
    F --> F1[UNIONPAY_WX_APP<br/>/v1/netpay/app/wx/order]
    F --> F2[UNIONPAY_WX_APP_MINI<br/>/v1/netpay/app/wx/mini-order]
    F --> F3[UNIONPAY_UAC_APP<br/>/v1/netpay/app/uac/order]
    
    G --> G1[UNIONPAY_C2B<br/>/v1/netpay/bills/qrcode/create]
    G --> G2[UNIONPAY_B2C<br/>/v1/netpay/poslink/pay]
    
    H --> H1[UNIONPAY_POS<br/>/v1/yunmis/do/action]
    
    I --> I1[UNIONPAY_ENTERPRISE<br/>transCode 202101]
```

> 套码策略：先用低费率mid → 失败自动fallback高费率mid

## 四、接口清单

### 4.1 商户入驻（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 图片上传 | ant.mybank.merchantprod.merchant.uploadphoto | →网商 |
| 入驻申请 | ant.mybank.merchantprod.merch.register | →网商 |
| 结果查询 | ant.mybank.merchantprod.merch.register.query | →网商 |
| 结果通知 | ant.mybank.merchantprod.merch.notify | ←网商 |
| 重发短信 | ant.mybank.bkmerchantprod.merch.applet.activityurl.send | →网商 |
| 商户信息查询 | ant.mybank.merchantprod.merch.query | →网商 |
| 信息修改 | ant.mybank.merchantprod.merch.updateMerchant | →网商 |
| 限权通知 | ant.mybank.merchantprod.merch.control.notify | ←网商 |
| 授权状态查询 | ant.mybank.merchantprod.merchant.arrangement.info.query | →网商 |
| 授权/解约通知 | ant.mybank.merchantprod.merchant.arrangement.info.notify | ←网商 |
| 解约申请审核 | ant.mybank.merchantprod.merchant.arrangement.audit | →网商 |

### 4.2 支付（银联）

| 接口 | URL | 方向 |
|------|-----|------|
| 微信小程序 | /v1/netpay/wx/unified-order | →银联 |
| 云闪付小程序 | /v1/netpay/uac/mini-order | →银联 |
| 微信H5 | /v1/netpay/h5/wx/mini-order | →银联 |
| 云闪付H5 | /v1/netpay/h5/uac/order | →银联 |
| 微信APP | /v1/netpay/app/wx/order | →银联 |
| 微信跳转小程序 | /v1/netpay/app/wx/mini-order | →银联 |
| 云闪付APP | /v1/netpay/app/uac/order | →银联 |
| C扫B主扫 | /v1/netpay/bills/qrcode/create | →银联 |
| B扫C被扫 | /v1/netpay/poslink/pay | →银联 |
| POS终端 | /v1/yunmis/do/action | →银联 |
| 企业订单生成 | transCode 202101 | →银联 |
| 支付查询 | /v1/netpay/query | →银联 |
| 企业订单查询 | transCode 202106 | →银联 |
| 订单关闭 | /v1/netpay/close | →银联 |
| 支付结果通知 | 回调notifyUrl | ←银联 |

### 4.3 支付（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 收银台创单 | ant.mybank.bkcloudfunds.unifiedorder.create | →网商 |
| 收银台查询 | ant.mybank.bkcloudfunds.unifiedorder.query | →网商 |
| 收银台通知 | ant.mybank.bkcloudfunds.unifiedorder.notify | ←网商 |
| 吱口令创建 | ant.mybank.bkcloudfunds.unifiedorder.sharetoken.create | →网商 |
| 补单创单 | ant.mybank.bkcloudfunds.bill.pay | →网商 |
| 补单作废 | ant.mybank.bkcloudfunds.bill.cancel | →网商 |
| 来账通知 | ant.mybank.bkcloudfunds.vostro.notify | ←网商 |
| 来账明细查询 | ant.mybank.bkcloudfunds.vostro.batchquery | →网商 |
| 渠道结算入账通知 | ant.mybank.bkcloudfunds.channel.vostro.notify | ←网商 |

### 4.4 退款（银联）

| 接口 | URL/transCode | 方向 |
|------|---------------|------|
| 网络退款 | /v1/netpay/refund | →银联 |
| 退款查询 | /v1/netpay/refund-query | →银联 |
| 企业退款 | transCode 202102 | →银联 |
| 退款撤销 | transCode 202110 | →银联 |
| 退货查询 | transCode 202109 | →银联 |
| 退款通知 | 回调notifyUrl | ←银联 |

### 4.5 退款（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 退款申请 | ant.mybank.bkcloudfunds.refund.apply | →网商 |
| 退款通知 | ant.mybank.bkcloudfunds.refund.notify | ←网商 |
| 退款查询 | ant.mybank.bkcloudfunds.refund.query | →网商 |
| 代扣退款 | ant.mybank.bkcloudfunds.protocol.withhold.refund.apply | →网商 |
| 代扣退款通知 | ant.mybank.bkcloudfunds.protocol.withhold.refund.result.notify | ←网商 |
| 代扣退款查询 | ant.mybank.bkcloudfunds.protocol.withhold.refund.query | →网商 |

### 4.6 提现（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 提现申请 | ant.mybank.bkcloudfunds.withdraw.apply | →网商 |
| 提现确认 | ant.mybank.bkcloudfunds.withdraw.applyconfirm | →网商 |
| 提现通知 | ant.mybank.bkcloudfunds.withdraw.notify | ←网商 |

### 4.7 清分（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 联动支付 | ant.mybank.bkcloudfunds.unifiedorder.create | →网商 |
| 签约申请 | ant.mybank.bkcloudfunds.agreement.withhold.sign | →网商 |
| 签约查询 | ant.mybank.bkcloudfunds.agreement.withhold.query | →网商 |
| 代扣申请 | ant.mybank.bkcloudfunds.order.withhold.apply | →网商 |
| 代扣通知 | ant.mybank.bkcloudfunds.protocol.withhold.result.notify | ←网商 |
| 代扣查询 | ant.mybank.bkcloudfunds.protocol.withhold.query | →网商 |
| 保证金扣除通知 | ant.mybank.bkcloudfunds.billpay.platform.deposit.notify | ←网商 |
| 退保申请 | ant.mybank.bkcloudbatch.stmt.deposit.return | →网商 |
| 退保查询 | ant.mybank.bkcloudbatch.stmt.deposit.return.query | →网商 |

### 4.8 冻结/解冻（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 冻结申请 | ant.mybank.bkcloudfunds.merchant.account.freeze.apply | →网商 |
| 解冻申请 | ant.mybank.bkcloudfunds.merchant.account.unfreeze.apply | →网商 |
| 解冻通知 | ant.mybank.bkcloudfunds.merchant.account.unfreeze.notify | ←网商 |
| 解冻查询 | ant.mybank.bkcloudfunds.merchant.balance.unfreeze.query | →网商 |

### 4.9 余额查询

| 接口 | Function/transCode | 方向 |
|------|-------------------|------|
| 网商余额 | ant.mybank.bkcloudfunds.balance.query | →网商 |
| 网商场景余额 | ant.mybank.bkcloudfunds.merchant.scene.balance.query | →网商 |
| 银联企业余额 | transCode 202108 | →银联 |

### 4.10 对账

| 接口 | Function | 方向 |
|------|----------|------|
| 网商对账文件 | ant.mybank.bkcloudfunds.recon.query | →网商 |
| 银联对账文件 | SFTP推送 | ←银联 |
| 银联操作记录 | transCode 202107 | →银联 |

### 4.11 电子回单（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 申请回单 | ant.mybank.bkcloudfunds.electronicreceipt.apply | →网商 |
| 批量申请 | ant.mybank.bkcloudfunds.electronicreceipt.batchapply | →网商 |
| 查询结果 | ant.mybank.bkcloudfunds.electronicreceipt.query | →网商 |
| 回单通知 | ant.mybank.bkcloudfunds.electronicreceipt.notify | ←网商 |

### 4.12 批次管理（网商）

| 接口 | Function | 方向 |
|------|----------|------|
| 创建批次 | ant.mybank.bkcloudbatch.batch.create | →网商 |
| 批次查询 | ant.mybank.bkcloudbatch.batch.query | →网商 |
| 批次完结通知 | ant.mybank.bkcloudfunds.billpay.batch.finish.notify | ←网商 |

### 4.13 POS终端（银联）

| 接口 | URL | 方向 |
|------|-----|------|
| 设备绑定 | /v1/yunmis/device/bang | →银联 |
| 指令下发 | /v1/yunmis/do/action | →银联 |
| 查询结果 | /v1/yunmis/pay/query | →银联 |
| 批量查询 | /v1/yunmis/pay/query-batch | →银联 |
| 回调通知 | urlNotify | ←银联 |

### 4.14 企业标记化（银联）

| 接口 | transCode | 方向 |
|------|-----------|------|
| 企业订单生成 | 202101 | →银联 |
| 企业订单退款 | 202102 | →银联 |
| 企业订单关闭 | 202103 | →银联 |
| 企业订单划付 | 202104 | →银联 |
| 企业订单分账 | 202105 | →银联 |
| 企业订单查询 | 202106 | →银联 |
| 操作记录查询 | 202107 | →银联 |
| 商户余额查询 | 202108 | →银联 |
| 退货订单查询 | 202109 | →银联 |
| 退款撤销 | 202110 | →银联 |
| 异步通知 | 回调notifyUrl | ←银联 |

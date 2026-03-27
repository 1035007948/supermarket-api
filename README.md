# 超市会员管理后台API接口

## 项目简介

会员管理，支持会员等级体系（普通/银卡/金卡），消费积分累计与抵扣，会员专属折扣/赠品，支持储值卡充值与消费。充值影响会员等级。

## 技术栈

- Java 1.8
- Spring Boot 2.3.12.RELEASE
- Spring Data JPA
- Spring Security
- JWT认证
- H2数据库（开发环境）
- MySQL（生产环境）
- Lombok

## 项目结构

```
src/main/java/com/supermarket/member/
├── config/              # 配置类
│   ├── PasswordConfig.java
│   └── SecurityConfig.java
├── controller/          # 控制器层
│   ├── AuthController.java
│   ├── MemberController.java
│   ├── StoredValueCardController.java
│   └── TransactionController.java
├── dto/                 # 数据传输对象
│   ├── ApiResponse.java
│   ├── ConsumptionRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── MemberRegisterRequest.java
│   └── RechargeRequest.java
├── entity/              # 实体类
│   ├── Member.java
│   ├── MemberLevel.java
│   ├── RechargeRecord.java
│   ├── StoredValueCard.java
│   ├── Transaction.java
│   └── TransactionType.java
├── exception/           # 异常处理
│   └── GlobalExceptionHandler.java
├── repository/          # 数据访问层
│   ├── MemberRepository.java
│   ├── RechargeRecordRepository.java
│   ├── StoredValueCardRepository.java
│   └── TransactionRepository.java
├── security/            # 安全认证
│   ├── JwtAuthenticationEntryPoint.java
│   └── JwtAuthenticationFilter.java
├── service/             # 业务逻辑层
│   ├── MemberService.java
│   ├── StoredValueCardService.java
│   └── TransactionService.java
└── util/                # 工具类
    └── JwtTokenUtil.java
```

## 会员等级体系

| 等级 | 名称 | 累计消费门槛 | 折扣率 | 积分倍率 |
|------|------|--------------|--------|----------|
| NORMAL | 普通会员 | 0元 | 1.0（无折扣） | 1倍 |
| SILVER | 银卡会员 | 1000元 | 0.95（95折） | 1.5倍 |
| GOLD | 金卡会员 | 5000元 | 0.9（9折） | 2倍 |

## API接口文档

### 认证接口

#### 1. 会员注册
```
POST /api/auth/register
Content-Type: application/json

请求体：
{
  "memberNo": "M001",
  "name": "张三",
  "phone": "13800138000",
  "password": "password123"
}

响应：
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": 1,
    "memberNo": "M001",
    "name": "张三",
    "phone": "13800138000",
    "level": "NORMAL",
    "points": 0,
    "storedBalance": 0
  }
}
```

#### 2. 会员登录
```
POST /api/auth/login
Content-Type: application/json

请求体：
{
  "memberNo": "M001",
  "password": "password123"
}

响应：
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "memberNo": "M001",
    "name": "张三",
    "level": "普通会员"
  }
}
```

### 会员管理接口

#### 3. 获取所有会员
```
GET /api/members
Authorization: Bearer {token}

响应：
{
  "success": true,
  "message": "操作成功",
  "data": [...]
}
```

#### 4. 根据ID获取会员
```
GET /api/members/{id}
Authorization: Bearer {token}
```

#### 5. 根据会员号获取会员
```
GET /api/members/memberNo/{memberNo}
Authorization: Bearer {token}
```

#### 6. 根据等级获取会员列表
```
GET /api/members/level/{level}
Authorization: Bearer {token}

level可选值：NORMAL, SILVER, GOLD
```

#### 7. 更新会员信息
```
PUT /api/members/{id}
Authorization: Bearer {token}
Content-Type: application/json

请求体：
{
  "name": "李四",
  "phone": "13900139000"
}
```

#### 8. 删除会员
```
DELETE /api/members/{id}
Authorization: Bearer {token}
```

### 交易接口

#### 9. 消费
```
POST /api/transactions/consume
Authorization: Bearer {token}
Content-Type: application/json

请求体：
{
  "memberId": 1,
  "amount": 100.00,
  "pointsToUse": 50,
  "description": "购买商品"
}

响应：
{
  "success": true,
  "message": "消费成功",
  "data": {
    "id": 1,
    "transactionNo": "TXN20260327...",
    "amount": 95.00,
    "pointsEarned": 95,
    "pointsUsed": 50
  }
}
```

#### 10. 充值
```
POST /api/transactions/recharge
Authorization: Bearer {token}
Content-Type: application/json

请求体：
{
  "memberId": 1,
  "amount": 500.00,
  "description": "储值卡充值"
}

响应：
{
  "success": true,
  "message": "充值成功",
  "data": {
    "id": 2,
    "transactionNo": "TXN20260327...",
    "amount": 500.00
  }
}
```

#### 11. 获取会员交易记录
```
GET /api/transactions/member/{memberId}
Authorization: Bearer {token}
```

#### 12. 根据交易号查询交易
```
GET /api/transactions/{transactionNo}
Authorization: Bearer {token}
```

### 储值卡接口

#### 13. 创建储值卡
```
POST /api/cards/member/{memberId}
Authorization: Bearer {token}
```

#### 14. 根据ID获取储值卡
```
GET /api/cards/{id}
Authorization: Bearer {token}
```

#### 15. 根据卡号获取储值卡
```
GET /api/cards/cardNo/{cardNo}
Authorization: Bearer {token}
```

#### 16. 获取会员的所有储值卡
```
GET /api/cards/member/{memberId}
Authorization: Bearer {token}
```

#### 17. 获取会员的激活储值卡
```
GET /api/cards/member/{memberId}/active
Authorization: Bearer {token}
```

#### 18. 停用储值卡
```
PUT /api/cards/{id}/deactivate
Authorization: Bearer {token}
```

#### 19. 激活储值卡
```
PUT /api/cards/{id}/activate
Authorization: Bearer {token}
```

## 运行项目

### 开发环境

```bash
# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

### 访问H2控制台

启动应用后，访问：http://localhost:8080/h2-console

- JDBC URL: jdbc:h2:mem:supermarket
- User: sa
- Password: (空)

## 测试

项目包含完整的单元测试和集成测试：

- MemberServiceTest：会员服务测试
- TransactionServiceTest：交易服务测试
- AuthControllerTest：认证控制器测试

运行测试：
```bash
mvn test
```

## 配置说明

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:supermarket
    driver-class-name: org.h2.Driver
    username: sa
    password: 

jwt:
  secret: supermarket-member-secret-key-2024
  expiration: 86400000  # 24小时
```

### 生产环境配置

切换到MySQL数据库：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/supermarket
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: update
```

## 业务规则

1. **会员等级自动升级**：根据累计消费金额自动升级会员等级
   - 累计消费 >= 5000元：金卡会员
   - 累计消费 >= 1000元：银卡会员
   - 累计消费 < 1000元：普通会员

2. **积分规则**：
   - 消费可获得积分，积分数量 = 消费金额 × 会员等级积分倍率
   - 积分可用于抵扣消费，100积分 = 1元

3. **折扣规则**：
   - 金卡会员：9折
   - 银卡会员：95折
   - 普通会员：无折扣

4. **储值卡**：
   - 充值金额会计入累计消费，影响会员等级
   - 每个会员可以有多个储值卡
   - 储值卡可以激活/停用

## 项目要求完成情况

✅ 使用Java 1.8 + Spring Boot实现RESTful接口输出
✅ 包含CRUD操作（会员管理、储值卡管理）
✅ 包含认证（JWT Token认证）
✅ 包含中间件（Spring Security过滤器）
✅ 包含数据库交互（Spring Data JPA）
✅ 生成测试类（单元测试和集成测试）

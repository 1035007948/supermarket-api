# 超市管理后台API接口

## 项目概述

基于Java + Spring Boot构建的超市管理后台RESTful API接口系统，包含商品管理、订单管理、会员管理三大核心模块。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - 数据持久化
- **Spring Security** - 安全认证
- **JWT** - Token认证
- **H2 Database** - 内存数据库（演示用）
- **Lombok** - 简化代码

## 项目结构

```
src/main/java/com/supermarket/
├── common/                    # 通用模块
│   ├── ApiResponse.java      # 统一响应封装
│   ├── BusinessException.java # 业务异常
│   └── GlobalExceptionHandler.java # 全局异常处理
├── config/                    # 配置类
│   ├── PasswordConfig.java   # 密码加密配置
│   └── SecurityConfig.java   # 安全配置
├── controller/               # 控制器层
│   ├── AuthController.java   # 认证控制器
│   ├── ProductController.java # 商品控制器
│   ├── OrderController.java  # 订单控制器
│   └── MemberController.java # 会员控制器
├── dto/                      # 数据传输对象
├── entity/                   # 实体类
├── repository/               # 数据访问层
├── scheduler/                # 定时任务
│   └── OrderNotificationScheduler.java # 订单通知调度
├── security/                 # 安全模块
│   ├── JwtUtil.java         # JWT工具类
│   └── JwtAuthenticationFilter.java # JWT过滤器
├── service/                  # 业务逻辑层
└── SupermarketApiApplication.java # 启动类
```

## 功能模块

### 1. 商品管理

**商品信息字段**
- 名称、价格、库存、规格
- 商品分类：FOOD(食品)、CLOTHING(服饰)、HOME(家居)、DIGITAL(数码)、BEAUTY(美妆)
- 商品状态：ON_SALE(在售)、OFF_SHELF(下架)、OUT_OF_STOCK(缺货)、PRE_SALE(预售)

**API接口**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/products | 创建商品 |
| GET | /api/products | 获取商品列表（分页） |
| GET | /api/products/{id} | 获取商品详情 |
| GET | /api/products/category/{category} | 按分类查询商品 |
| GET | /api/products/status/{status} | 按状态查询商品 |
| GET | /api/products/search?name=xxx | 搜索商品 |
| PUT | /api/products/{id} | 更新商品 |
| DELETE | /api/products/{id} | 删除商品 |
| PATCH | /api/products/{id}/stock | 更新库存 |

### 2. 订单管理

**支付方式**
- ONLINE_PAYMENT(线上支付)
- IN_STORE_PAYMENT(到店付款)

**订单状态流转**
```
待支付(PENDING_PAYMENT) 
    → 待备货(PREPARING) 
    → 待取货(READY_FOR_PICKUP) / 待发货(READY_FOR_DELIVERY) 
    → 已完成(COMPLETED) / 已取消(CANCELLED)
```

**退款流程**
```
已完成(COMPLETED) → 退款中(REFUNDING) → 已退款(REFUNDED)
```

**API接口**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/orders | 创建订单 |
| GET | /api/orders | 获取订单列表 |
| GET | /api/orders/{id} | 获取订单详情 |
| GET | /api/orders/order-no/{orderNo} | 按订单号查询 |
| GET | /api/orders/member/{memberId} | 按会员查询订单 |
| GET | /api/orders/status/{status} | 按状态查询订单 |
| POST | /api/orders/{id}/pay | 支付订单 |
| POST | /api/orders/{id}/prepare | 备货完成 |
| POST | /api/orders/{id}/deliver | 发货 |
| POST | /api/orders/{id}/complete | 完成订单 |
| POST | /api/orders/{id}/cancel | 取消订单 |
| POST | /api/orders/{id}/refund | 申请退款 |
| POST | /api/orders/{id}/approve-refund | 批准退款 |

**订单通知**
- 订单发货/备货完成后15分钟自动提醒通知（定时任务）

### 3. 会员管理

**会员等级体系**
- NORMAL(普通会员)
- SILVER(银卡会员)：消费满5000元
- GOLD(金卡会员)：消费满10000元

**会员权益**
- 消费积分累计（消费金额的10%）
- 积分抵扣（1积分=0.01元）
- 会员专属折扣：银卡5%、金卡10%
- 储值卡充值赠送：充值200送5%，充值500送10%

**API接口**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/members | 创建会员 |
| GET | /api/members | 获取会员列表 |
| GET | /api/members/{id} | 获取会员详情 |
| GET | /api/members/phone/{phone} | 按手机号查询 |
| GET | /api/members/level/{level} | 按等级查询 |
| PUT | /api/members/{id} | 更新会员 |
| DELETE | /api/members/{id} | 删除会员 |
| POST | /api/members/{id}/recharge | 储值卡充值 |
| POST | /api/members/{id}/points/add | 增加积分 |
| POST | /api/members/{id}/points/use | 使用积分 |
| GET | /api/members/{id}/transactions | 查询交易记录 |

### 4. 认证模块

**API接口**

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/login | 用户登录 |

**认证方式**
- JWT Token认证
- 请求头添加：`Authorization: Bearer {token}`

## 运行项目

### 前置条件
- JDK 17+
- Maven 3.6+

### 运步骤

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 打包项目
mvn clean package
```

### 访问地址

- API地址：http://localhost:8080
- H2控制台：http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:supermarket
  - 用户名: sa
  - 密码: (空)

## API请求示例

### 用户注册
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 创建商品
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "name":"苹果",
    "price":5.50,
    "stock":100,
    "specification":"500g/袋",
    "category":"FOOD",
    "status":"ON_SALE"
  }'
```

### 创建会员
```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "name":"张三",
    "phone":"13800138000",
    "email":"zhangsan@example.com"
  }'
```

### 创建订单
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "memberId":1,
    "paymentMethod":"ONLINE_PAYMENT",
    "items":[
      {"productId":1,"quantity":2}
    ],
    "deliveryType":"PICKUP"
  }'
```

## 数据库设计

### 商品表(products)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 商品名称 |
| price | DECIMAL | 价格 |
| stock | INT | 库存 |
| specification | VARCHAR | 规格 |
| category | ENUM | 分类 |
| status | ENUM | 状态 |

### 订单表(orders)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_no | VARCHAR | 订单号 |
| member_id | BIGINT | 会员ID |
| payment_method | ENUM | 支付方式 |
| status | ENUM | 订单状态 |
| total_amount | DECIMAL | 总金额 |

### 会员表(members)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR | 姓名 |
| phone | VARCHAR | 手机号 |
| level | ENUM | 等级 |
| points | INT | 积分 |
| balance | DECIMAL | 余额 |

## 项目特性

✅ RESTful API设计
✅ JWT Token认证
✅ 统一响应格式
✅ 全局异常处理
✅ 参数校验
✅ 分页查询
✅ 定时任务调度
✅ 事务管理
✅ 枚举类型使用

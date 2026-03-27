# 数据库部署文档

## 数据库信息

- **数据库名称**: supermarket_db
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **支持数据库**: MySQL 8.0+

## 部署步骤

### 1. 创建数据库并导入表结构

```bash
# 登录MySQL
mysql -u root -p

# 执行建表脚本
source /path/to/schema.sql
```

### 2. 导入测试数据（可选）

```bash
# 导入测试数据
source /path/to/test-data.sql
```

### 3. 创建应用数据库用户

```sql
-- 创建用户
CREATE USER 'supermarket'@'%' IDENTIFIED BY 'your_password';

-- 授权
GRANT ALL PRIVILEGES ON supermarket_db.* TO 'supermarket'@'%';

-- 刷新权限
FLUSH PRIVILEGES;
```

## 数据表说明

### 核心业务表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| users | 用户表 | id, username, password, role |
| products | 商品表 | id, name, price, stock, category, status |
| members | 会员表 | id, name, phone, level, points, balance |
| orders | 订单表 | id, order_no, member_id, status, total_amount |
| order_items | 订单项表 | id, order_id, product_id, quantity, subtotal |
| member_transactions | 会员交易记录表 | id, member_id, type, amount, points |

### 枚举值说明

**商品分类 (ProductCategory)**
- FOOD: 食品
- CLOTHING: 服饰
- HOME: 家居
- DIGITAL: 数码
- BEAUTY: 美妆

**商品状态 (ProductStatus)**
- ON_SALE: 在售
- OFF_SHELF: 下架
- OUT_OF_STOCK: 缺货
- PRE_SALE: 预售

**会员等级 (MemberLevel)**
- NORMAL: 普通会员
- SILVER: 银卡会员（累计消费≥5000元）
- GOLD: 金卡会员（累计消费≥10000元）

**订单状态 (OrderStatus)**
- PENDING_PAYMENT: 待支付
- PREPARING: 待备货
- READY_FOR_PICKUP: 待取货
- READY_FOR_DELIVERY: 待发货
- COMPLETED: 已完成
- CANCELLED: 已取消
- REFUNDING: 退款中
- REFUNDED: 已退款

**支付方式 (PaymentMethod)**
- ONLINE_PAYMENT: 线上支付
- IN_STORE_PAYMENT: 到店付款

**交易类型 (TransactionType)**
- RECHARGE: 充值
- CONSUME: 消费
- REFUND: 退款
- POINTS_EARN: 积分获得
- POINTS_USE: 积分使用
- DISCOUNT: 折扣

## 数据库视图

### v_order_details - 订单详情视图
提供订单的完整信息，包括会员信息、商品数量等。

### v_product_sales - 商品销售统计视图
统计每个商品的销售数量和销售额。

## 存储过程

### sp_update_member_level
自动更新会员等级，根据累计消费金额。

**参数**:
- p_member_id: 会员ID

**调用示例**:
```sql
CALL sp_update_member_level(1);
```

### sp_order_statistics
统计指定日期范围内的订单数据。

**参数**:
- p_start_date: 开始日期
- p_end_date: 结束日期

**调用示例**:
```sql
CALL sp_order_statistics('2024-01-01', '2024-01-31');
```

## 触发器

### tr_order_complete_points
订单完成后自动为会员增加积分。

**触发条件**: 订单状态更新为 COMPLETED

**执行逻辑**:
1. 计算积分（实付金额的10%）
2. 更新会员积分
3. 记录交易明细

## 索引说明

### 主键索引
所有表都有自增主键 `id`

### 业务索引
- users: username（唯一索引）
- products: category, status, name
- members: phone（唯一索引）, level
- orders: order_no（唯一索引）, member_id, status, created_at
- order_items: order_id, product_id
- member_transactions: member_id, type, created_at

### 复合索引
- orders: idx_member_status (member_id, status)
- orders: idx_created_status (created_at, status)
- products: idx_category_status (category, status)

## 数据备份与恢复

### 备份
```bash
# 全库备份
mysqldump -u root -p supermarket_db > backup_$(date +%Y%m%d).sql

# 仅备份表结构
mysqldump -u root -p --no-data supermarket_db > schema_backup.sql

# 仅备份数据
mysqldump -u root -p --no-create-info supermarket_db > data_backup.sql
```

### 恢复
```bash
mysql -u root -p supermarket_db < backup_20240101.sql
```

## 性能优化建议

1. **定期清理历史数据**
   - 清理90天前的已取消订单
   - 清理180天前的交易记录

2. **索引优化**
   - 根据实际查询情况调整索引
   - 定期分析慢查询日志

3. **分表策略**
   - 订单表按时间分表（如按月）
   - 交易记录表按时间分表

4. **读写分离**
   - 主库负责写操作
   - 从库负责读操作

## 安全建议

1. **数据库用户权限最小化**
   - 应用用户仅授予必要权限
   - 禁止使用root用户运行应用

2. **数据加密**
   - 密码使用BCrypt加密
   - 敏感信息加密存储

3. **SQL注入防护**
   - 使用参数化查询
   - 禁止拼接SQL

4. **定期备份**
   - 每日自动备份
   - 备份文件加密存储

## 监控指标

1. **连接数监控**
   - 最大连接数
   - 当前连接数

2. **查询性能**
   - 慢查询数量
   - 查询响应时间

3. **存储空间**
   - 表大小
   - 索引大小
   - 剩余空间

4. **主从延迟**（如使用主从复制）
   - 复制延迟时间
   - 从库状态

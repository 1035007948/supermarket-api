# 数据库部署说明

## 文件说明

| 文件 | 说明 |
|------|------|
| [deploy.sql](file:///d:/workspace-bbt/doubao/supermarket-api/sql/deploy.sql) | 完整部署脚本，包含建表、初始化数据、存储过程等 |
| [clean.sql](file:///d:/workspace-bbt/doubao/supermarket-api/sql/clean.sql) | 数据清理脚本，清空所有表数据 |
| [uninstall.sql](file:///d:/workspace-bbt/doubao/supermarket-api/sql/uninstall.sql) | 卸载脚本，删除整个数据库 |

## 部署步骤

### 1. 环境要求

- MySQL 5.7+ 或 MySQL 8.0+
- 字符集：utf8mb4
- 排序规则：utf8mb4_unicode_ci

### 2. 执行部署脚本

```bash
# 方式一：使用MySQL命令行
mysql -u root -p < sql/deploy.sql

# 方式二：登录MySQL后执行
mysql -u root -p
source /path/to/deploy.sql
```

### 3. 验证部署

```sql
-- 查看数据库
SHOW DATABASES LIKE 'supermarket_member';

-- 查看表结构
USE supermarket_member;
SHOW TABLES;

-- 查看初始化数据
SELECT * FROM members;
SELECT * FROM stored_value_cards;
SELECT * FROM transactions;
```

## 数据库结构

### 表结构

```
supermarket_member/
├── members                # 会员信息表
├── stored_value_cards     # 储值卡表
├── transactions           # 交易记录表
└── recharge_records       # 充值记录表
```

### 表关系

```
members (1) ----< (N) stored_value_cards
members (1) ----< (N) transactions
members (1) ----< (N) recharge_records
stored_value_cards (1) ----< (N) recharge_records
```

## 初始化数据

部署脚本会自动创建3个测试会员：

| 会员号 | 姓名 | 等级 | 累计消费 | 积分 | 储值余额 |
|--------|------|------|----------|------|----------|
| M001 | 张三 | NORMAL | 0.00 | 0 | 0.00 |
| M002 | 李四 | SILVER | 1500.00 | 150 | 500.00 |
| M003 | 王五 | GOLD | 6000.00 | 600 | 1000.00 |

**默认密码**：password123（BCrypt加密）

## 存储过程

### 1. 会员等级自动升级

```sql
CALL sp_update_member_level(1);  -- 参数：会员ID
```

### 2. 消费处理

```sql
CALL sp_process_consumption(
    1,          -- 会员ID
    100.00,     -- 消费金额
    50,         -- 使用积分
    @trans_no,  -- 输出：交易流水号
    @points,    -- 输出：获得积分
    @amount     -- 输出：实际支付金额
);

SELECT @trans_no, @points, @amount;
```

### 3. 充值处理

```sql
CALL sp_process_recharge(
    1,          -- 会员ID
    500.00,     -- 充值金额
    @trans_no,  -- 输出：交易流水号
    @card_no    -- 输出：储值卡号
);

SELECT @trans_no, @card_no;
```

## 视图

### 1. 会员统计视图

```sql
SELECT * FROM v_member_statistics;
```

### 2. 会员等级统计视图

```sql
SELECT * FROM v_level_statistics;
```

## 应用配置

部署完成后，修改Spring Boot配置文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/supermarket_member?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: validate  # 生产环境建议使用validate
    show-sql: false
```

## 数据清理

**警告**：此操作将清空所有数据！

```bash
mysql -u root -p < sql/clean.sql
```

## 卸载数据库

**警告**：此操作将删除整个数据库！

```bash
mysql -u root -p < sql/uninstall.sql
```

## 备份与恢复

### 备份数据库

```bash
mysqldump -u root -p supermarket_member > backup_$(date +%Y%m%d).sql
```

### 恢复数据库

```bash
mysql -u root -p supermarket_member < backup_20260327.sql
```

## 性能优化建议

1. **索引优化**：脚本已创建必要的索引，可根据实际查询情况调整
2. **分区表**：对于大量交易记录，建议按时间分区
3. **读写分离**：建议配置主从复制，实现读写分离
4. **缓存**：使用Redis缓存会员信息和热点数据

## 安全建议

1. 创建专用数据库用户，不要使用root账户
2. 限制用户权限，只授予必要的CRUD权限
3. 定期备份数据库
4. 启用SSL连接
5. 定期更新密码

## 常见问题

### 1. 字符集问题

确保数据库、表、字段都使用utf8mb4字符集：

```sql
ALTER DATABASE supermarket_member CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 外键约束错误

检查数据删除顺序，或临时禁用外键检查：

```sql
SET FOREIGN_KEY_CHECKS = 0;
-- 执行操作
SET FOREIGN_KEY_CHECKS = 1;
```

### 3. 存储过程权限

确保用户有执行存储过程的权限：

```sql
GRANT EXECUTE ON supermarket_member.* TO 'supermarket_app'@'%';
FLUSH PRIVILEGES;
```

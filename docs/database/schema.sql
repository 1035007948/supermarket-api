-- =====================================================
-- 超市管理后台数据库部署脚本
-- 数据库类型: MySQL 8.0+
-- 生成时间: 2024-03-27
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE supermarket_db;

-- -----------------------------------------------------
-- 用户表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `name` VARCHAR(50) NULL COMMENT '姓名',
    `email` VARCHAR(100) NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------
-- 会员表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `name` VARCHAR(50) NOT NULL COMMENT '会员姓名',
    `email` VARCHAR(100) NULL COMMENT '邮箱',
    `level` VARCHAR(20) NOT NULL DEFAULT 'REGULAR' COMMENT '会员等级: REGULAR(普通), SILVER(银卡), GOLD(金卡)',
    `points` INT NOT NULL DEFAULT 0 COMMENT '会员积分',
    `total_consumption` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    `birthday` DATE NULL COMMENT '生日',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- -----------------------------------------------------
-- 商品表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock` INT NOT NULL COMMENT '库存数量',
    `specification` VARCHAR(50) NULL COMMENT '商品规格',
    `category` VARCHAR(20) NOT NULL COMMENT '商品分类: FOOD(食品), CLOTHING(服饰), HOME(家居), DIGITAL(数码), BEAUTY(美妆)',
    `status` VARCHAR(20) NOT NULL COMMENT '商品状态: ON_SALE(在售), OFF_SHELF(下架), OUT_OF_STOCK(缺货), PRE_SALE(预售)',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- -----------------------------------------------------
-- 订单表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `member_id` BIGINT NULL COMMENT '会员ID',
    `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(12,2) NULL COMMENT '优惠金额',
    `pay_amount` DECIMAL(12,2) NOT NULL COMMENT '实付金额',
    `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式: ONLINE_PAYMENT(线上支付), IN_STORE_PAYMENT(到店付款)',
    `status` VARCHAR(20) NOT NULL COMMENT '订单状态: PENDING_PAYMENT(待支付), PENDING_PREPARATION(待备货), PENDING_PICKUP(待取货), PENDING_SHIPMENT(待发货), COMPLETED(已完成), CANCELLED(已取消)',
    `pay_time` DATETIME NULL COMMENT '支付时间',
    `ship_time` DATETIME NULL COMMENT '发货时间',
    `complete_time` DATETIME NULL COMMENT '完成时间',
    `cancel_time` DATETIME NULL COMMENT '取消时间',
    `remark` VARCHAR(500) NULL COMMENT '订单备注',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    `reminder_sent` TINYINT(1) DEFAULT 0 COMMENT '是否已发送提醒',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- -----------------------------------------------------
-- 订单项表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_price` DECIMAL(12,2) NOT NULL COMMENT '商品总价',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- -----------------------------------------------------
-- 储值卡表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `stored_value_card`;
CREATE TABLE `stored_value_card` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `card_no` VARCHAR(32) NOT NULL COMMENT '卡号',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `balance` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    `total_recharge` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计充值金额',
    `total_consumption` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活',
    `expire_time` DATETIME NULL COMMENT '过期时间',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_no` (`card_no`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='储值卡表';

-- -----------------------------------------------------
-- 储值卡交易记录表
-- -----------------------------------------------------
DROP TABLE IF EXISTS `stored_value_card_transaction`;
CREATE TABLE `stored_value_card_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `card_id` BIGINT NOT NULL COMMENT '储值卡ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `amount` DECIMAL(12,2) NOT NULL COMMENT '交易金额',
    `type` VARCHAR(20) NOT NULL COMMENT '交易类型: RECHARGE(充值), CONSUMPTION(消费)',
    `remark` VARCHAR(200) NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL COMMENT '交易时间',
    `order_id` BIGINT NULL COMMENT '订单ID',
    PRIMARY KEY (`id`),
    KEY `idx_card_id` (`card_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='储值卡交易记录表';

-- -----------------------------------------------------
-- 初始化数据
-- -----------------------------------------------------

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `name`, `role`, `is_active`, `create_time`)
VALUES ('admin', '$2a$10$eK8Z.Rd8Z8eK8Z8eK8Z8eu', '管理员', 'ADMIN', 1, NOW());

-- 插入测试会员数据
INSERT INTO `member` (`phone`, `name`, `level`, `points`, `total_consumption`, `is_active`, `create_time`)
VALUES 
('13800138001', '张三', 'REGULAR', 0, 0.00, 1, NOW()),
('13800138002', '李四', 'REGULAR', 0, 0.00, 1, NOW()),
('13800138003', '王五', 'SILVER', 1500, 1500.00, 1, NOW()),
('13800138004', '赵六', 'GOLD', 6000, 6000.00, 1, NOW());

-- 插入测试商品数据
INSERT INTO `product` (`name`, `price`, `stock`, `specification`, `category`, `status`, `create_time`)
VALUES
('苹果', '8.99', '100', '500g', 'FOOD', 'ON_SALE', NOW()),
('牛仔裤', '99.00', '50', 'L', 'CLOTHING', 'ON_SALE', NOW()),
('智能音箱', '299.00', '30', '标准版', 'DIGITAL', 'ON_SALE', NOW()),
('沙发', '1999.00', '10', '三人座', 'HOME', 'ON_SALE', NOW()),
('面膜', '69.90', '200', '10片装', 'BEAUTY', 'ON_SALE', NOW()),
('牛奶', '59.90', '80', '1L*6盒', 'FOOD', 'ON_SALE', NOW()),
('T恤', '49.90', '100', 'XL', 'CLOTHING', 'ON_SALE', NOW()),
('手机支架', '19.90', '150', '可调节', 'DIGITAL', 'ON_SALE', NOW()),
('洗衣液', '29.90', '100', '2kg', 'HOME', 'ON_SALE', NOW()),
('口红', '168.00', '60', '#D24', 'BEAUTY', 'ON_SALE', NOW());

-- 插入测试储值卡数据
INSERT INTO `stored_value_card` (`card_no`, `member_id`, `balance`, `total_recharge`, `total_consumption`, `is_active`, `create_time`)
VALUES
('SV0001', 1, 500.00, 500.00, 0.00, 1, NOW()),
('SV0002', 2, 1000.00, 1000.00, 0.00, 1, NOW()),
('SV0003', 3, 2000.00, 2000.00, 500.00, 1, NOW()),
('SV0004', 4, 5000.00, 5000.00, 1000.00, 1, NOW());

-- 创建索引优化
CREATE INDEX `idx_product_category` ON `product`(`category`);
CREATE INDEX `idx_product_status` ON `product`(`status`);
CREATE INDEX `idx_member_level` ON `member`(`level`);
CREATE INDEX `idx_transaction_create_time` ON `stored_value_card_transaction`(`create_time`);

-- 显示创建结果
SELECT '数据库创建完成！' AS message;
SELECT COUNT(*) AS user_count FROM `user`;
SELECT COUNT(*) AS member_count FROM `member`;
SELECT COUNT(*) AS product_count FROM `product`;
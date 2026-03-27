-- =====================================================
-- 超市管理后台数据库部署脚本
-- 数据库类型: MySQL 8.0+
-- 生成时间: 2024-03-27
-- 版本: v1.0
-- 描述: 超市管理系统数据库完整数据库脚本
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 创建数据库
-- ----------------------------
DROP DATABASE IF EXISTS `supermarket_db`;
CREATE DATABASE IF NOT EXISTS `supermarket_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `supermarket_db`;

-- ----------------------------
-- 2. 用户表 (user)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `name` VARCHAR(50) NULL DEFAULT NULL COMMENT '姓名',
    `email` VARCHAR(100) NULL DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色：ADMIN(管理员), USER(普通用户)',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活：1激活，0禁用',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表';

-- ----------------------------
-- 3. 会员表 (member)
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号（登录账号）',
    `name` VARCHAR(50) NOT NULL COMMENT '会员姓名',
    `email` VARCHAR(100) NULL DEFAULT NULL COMMENT '邮箱',
    `level` VARCHAR(20) NOT NULL DEFAULT 'REGULAR' COMMENT '会员等级：REGULAR(普通), SILVER(银卡), GOLD(金卡)',
    `points` INT NOT NULL DEFAULT 0 COMMENT '会员积分',
    `total_consumption` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    `birthday` DATE NULL DEFAULT NULL COMMENT '生日',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否激活：1激活，0禁用',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_phone`(`phone`) USING BTREE,
    INDEX `idx_level`(`level`) USING BTREE,
    INDEX `idx_points`(`points`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会员表';

-- ----------------------------
-- 4. 商品表 (product)
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    `stock` INT NOT NULL COMMENT '库存数量',
    `specification` VARCHAR(50) NULL DEFAULT NULL COMMENT '商品规格',
    `category` VARCHAR(20) NOT NULL COMMENT '商品分类：FOOD(食品), CLOTHING(服饰), HOME(家居), DIGITAL(数码), BEAUTY(美妆)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ON_SALE' COMMENT '商品状态：ON_SALE(在售), OFF_SHELF(下架), OUT_OF_STOCK(缺货), PRE_SALE(预售)',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_category`(`category`) USING BTREE,
    INDEX `idx_status`(`status`) USING BTREE,
    INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表';

-- ----------------------------
-- 5. 订单表 (orders)
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号（唯一）',
    `member_id` BIGINT NULL DEFAULT NULL COMMENT '会员ID（非会员下单可为空）',
    `total_amount` DECIMAL(12, 2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(12, 2) NULL DEFAULT 0.00 COMMENT '优惠金额',
    `pay_amount` DECIMAL(12, 2) NOT NULL COMMENT '实付金额',
    `payment_method` VARCHAR(20) NULL DEFAULT NULL COMMENT '支付方式：ONLINE_PAYMENT(线上支付), IN_STORE_PAYMENT(到店付款)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态：PENDING_PAYMENT(待支付), PENDING_PREPARATION(待备货), PENDING_PICKUP(待取货), PENDING_SHIPMENT(待发货), COMPLETED(已完成), CANCELLED(已取消)',
    `pay_time` DATETIME NULL DEFAULT NULL COMMENT '支付时间',
    `ship_time` DATETIME NULL DEFAULT NULL COMMENT '发货/备货完成时间',
    `complete_time` DATETIME NULL DEFAULT NULL COMMENT '订单完成时间',
    `cancel_time` DATETIME NULL DEFAULT NULL COMMENT '订单取消时间',
    `remark` VARCHAR(500) NULL DEFAULT NULL COMMENT '订单备注',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除：1已删除，0未删除',
    `reminder_sent` TINYINT(1) DEFAULT 0 COMMENT '提醒是否已发送：1已发送，0未发送',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
    INDEX `idx_member_id`(`member_id`) USING BTREE,
    INDEX `idx_status`(`status`) USING BTREE,
    INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表';

-- ----------------------------
-- 6. 订单项表 (order_item)
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称（冗余存储）',
    `product_price` DECIMAL(10, 2) NOT NULL COMMENT '商品单价（冗余存储）',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_price` DECIMAL(12, 2) NOT NULL COMMENT '商品总价',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_order_id`(`order_id`) USING BTREE,
    INDEX `idx_product_id`(`product_id`) USING BTREE,
    CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单项表';

-- ----------------------------
-- 7.
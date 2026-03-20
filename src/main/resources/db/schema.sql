-- 超市管理后台数据库表结构

-- 用户表（管理员）
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `role` VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色：ADMIN-管理员',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `code` VARCHAR(30) NOT NULL COMMENT '分类编码',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `code` VARCHAR(100) NOT NULL COMMENT '商品编码',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
    `cost_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '成本价',
    `stock` INT DEFAULT 0 COMMENT '库存数量',
    `specification` VARCHAR(200) COMMENT '规格',
    `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
    `description` TEXT COMMENT '商品描述',
    `main_image` VARCHAR(500) COMMENT '主图URL',
    `images` JSON COMMENT '图片列表',
    `status` VARCHAR(20) DEFAULT 'ON_SALE' COMMENT '状态：ON_SALE-在售，OFF_SHELF-下架，OUT_OF_STOCK-缺货，PRE_SALE-预售',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 会员表
CREATE TABLE IF NOT EXISTS `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_no` VARCHAR(50) NOT NULL COMMENT '会员编号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` DATE COMMENT '生日',
    `level` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '等级：NORMAL-普通，SILVER-银卡，GOLD-金卡',
    `points` INT DEFAULT 0 COMMENT '积分',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '储值余额',
    `total_consumption` DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计消费',
    `password` VARCHAR(100) COMMENT '密码',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `register_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_no` (`member_no`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 会员等级配置表
CREATE TABLE IF NOT EXISTS `member_level_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `level_code` VARCHAR(20) NOT NULL COMMENT '等级编码',
    `level_name` VARCHAR(50) NOT NULL COMMENT '等级名称',
    `min_points` INT DEFAULT 0 COMMENT '最低积分要求',
    `min_consumption` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费要求',
    `discount_rate` DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率',
    `points_rate` DECIMAL(3,2) DEFAULT 1.00 COMMENT '积分倍率',
    `benefits` VARCHAR(500) COMMENT '权益说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级配置表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS `points_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：EARN-获得，DEDUCT-抵扣，EXPIRE-过期',
    `points` INT NOT NULL COMMENT '积分数量',
    `balance` INT NOT NULL COMMENT '变动后余额',
    `source` VARCHAR(50) COMMENT '来源',
    `source_id` BIGINT COMMENT '来源ID',
    `remark` VARCHAR(200) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_member` (`member_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 储值记录表
CREATE TABLE IF NOT EXISTS `balance_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `member_id` BIGINT NOT NULL COMMENT '会员ID',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：RECHARGE-充值，CONSUME-消费，REFUND-退款',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `balance` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    `source` VARCHAR(50) COMMENT '来源',
    `source_id` BIGINT COMMENT '来源ID',
    `remark` VARCHAR(200) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_member` (`member_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='储值记录表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `member_id` BIGINT COMMENT '会员ID（非会员为空）',
    `member_name` VARCHAR(50) COMMENT '会员姓名',
    `member_phone` VARCHAR(20) COMMENT '会员手机号',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `points_deduct` INT DEFAULT 0 COMMENT '积分抵扣',
    `payable_amount` DECIMAL(10,2) NOT NULL COMMENT '应付金额',
    `pay_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
    `pay_type` VARCHAR(20) COMMENT '支付方式：ONLINE-线上支付，OFFLINE-到店付款',
    `pay_status` VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付，PAID-已支付，REFUNDED-已退款',
    `status` VARCHAR(20) DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态：PENDING_PAYMENT-待支付，PENDING_PREPARE-待备货，PENDING_PICKUP-待取货，PENDING_SHIP-待发货，SHIPPED-已发货，COMPLETED-已完成，CANCELLED-已取消',
    `delivery_type` VARCHAR(20) DEFAULT 'PICKUP' COMMENT '配送方式：PICKUP-自提，DELIVERY-配送',
    `receiver_name` VARCHAR(50) COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) COMMENT '收货人电话',
    `receiver_address` VARCHAR(500) COMMENT '收货地址',
    `remark` VARCHAR(500) COMMENT '订单备注',
    `cancel_reason` VARCHAR(200) COMMENT '取消原因',
    `paid_time` DATETIME COMMENT '支付时间',
    `prepared_time` DATETIME COMMENT '备货完成时间',
    `shipped_time` DATETIME COMMENT '发货时间',
    `completed_time` DATETIME COMMENT '完成时间',
    `cancelled_time` DATETIME COMMENT '取消时间',
    `notify_time` DATETIME COMMENT '通知时间',
    `notify_status` TINYINT DEFAULT 0 COMMENT '通知状态：0-未通知，1-已通知',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_member` (`member_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_code` VARCHAR(100) COMMENT '商品编码',
    `product_image` VARCHAR(500) COMMENT '商品图片',
    `specification` VARCHAR(200) COMMENT '规格',
    `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order` (`order_id`),
    KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS `refund_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `refund_no` VARCHAR(50) NOT NULL COMMENT '退款编号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `member_id` BIGINT COMMENT '会员ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(500) COMMENT '退款原因',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
    `reviewer_id` BIGINT COMMENT '审核人ID',
    `reviewer_name` VARCHAR(50) COMMENT '审核人姓名',
    `review_remark` VARCHAR(500) COMMENT '审核备注',
    `review_time` DATETIME COMMENT '审核时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order` (`order_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 初始化数据
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'ADMIN');

INSERT INTO `category` (`name`, `code`, `sort_order`) VALUES
('食品', 'FOOD', 1),
('服饰', 'CLOTHING', 2),
('家居', 'HOME', 3),
('数码', 'DIGITAL', 4),
('美妆', 'BEAUTY', 5);

INSERT INTO `member_level_config` (`level_code`, `level_name`, `min_points`, `min_consumption`, `discount_rate`, `points_rate`, `benefits`) VALUES
('NORMAL', '普通会员', 0, 0, 1.00, 1.00, '基础积分累计'),
('SILVER', '银卡会员', 1000, 1000.00, 0.95, 1.20, '95折优惠，1.2倍积分'),
('GOLD', '金卡会员', 5000, 5000.00, 0.90, 1.50, '9折优惠，1.5倍积分，专属赠品');

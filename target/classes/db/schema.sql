-- 超市会员管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket_member DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE supermarket_member;

-- 会员表
CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    member_no VARCHAR(32) NOT NULL UNIQUE COMMENT '会员编号',
    name VARCHAR(50) NOT NULL COMMENT '会员姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    password VARCHAR(255) COMMENT '密码',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATETIME COMMENT '生日',
    address VARCHAR(255) COMMENT '地址',
    level INT DEFAULT 0 COMMENT '会员等级：0-普通，1-银卡，2-金卡',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '储值余额',
    points INT DEFAULT 0 COMMENT '可用积分',
    total_points INT DEFAULT 0 COMMENT '累计积分',
    total_consumption DECIMAL(10,2) DEFAULT 0.00 COMMENT '累计消费金额',
    register_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    last_consumption_time DATETIME COMMENT '最后消费时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，2-冻结，3-过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_phone (phone),
    INDEX idx_member_no (member_no),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- 会员等级表
CREATE TABLE IF NOT EXISTS member_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
    level_code INT NOT NULL UNIQUE COMMENT '等级编码：0-普通，1-银卡，2-金卡',
    min_points INT DEFAULT 0 COMMENT '所需最小积分',
    max_points INT DEFAULT 0 COMMENT '所需最大积分',
    min_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '所需最小余额',
    discount_rate DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率',
    benefits TEXT COMMENT '权益说明',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type TINYINT NOT NULL COMMENT '类型：1-获得，2-抵扣，3-过期',
    points INT NOT NULL COMMENT '积分数量',
    before_points INT DEFAULT 0 COMMENT '操作前积分',
    after_points INT DEFAULT 0 COMMENT '操作后积分',
    amount DECIMAL(10,2) COMMENT '关联金额',
    source VARCHAR(50) COMMENT '来源',
    order_no VARCHAR(64) COMMENT '订单号',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_member_id (member_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 余额记录表
CREATE TABLE IF NOT EXISTS balance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type TINYINT NOT NULL COMMENT '类型：1-充值，2-消费，3-退款',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    before_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '操作前余额',
    after_balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '操作后余额',
    source VARCHAR(50) COMMENT '来源',
    order_no VARCHAR(64) COMMENT '订单号',
    payment_method VARCHAR(50) COMMENT '支付方式',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_member_id (member_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额记录表';

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 初始化会员等级数据
INSERT INTO member_level (level_name, level_code, min_points, max_points, min_balance, discount_rate, benefits, description, status) VALUES
('普通会员', 0, 0, 999, 0.00, 0.95, '享受9.5折优惠', '普通会员等级', 1),
('银卡会员', 1, 1000, 4999, 0.00, 0.90, '享受9折优惠，生日礼品', '银卡会员等级', 1),
('金卡会员', 2, 5000, 999999999, 0.00, 0.85, '享受8.5折优惠，生日礼品，专属客服', '金卡会员等级', 1);

-- 初始化系统用户（密码：admin123）
INSERT INTO sys_user (username, password, real_name, phone, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '系统管理员', '13800138000', 'admin@supermarket.com', 'ADMIN', 1),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '普通用户', '13800138001', 'user@supermarket.com', 'USER', 1);

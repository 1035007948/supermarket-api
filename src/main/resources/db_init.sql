-- 超市会员管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE supermarket;

-- 会员表
CREATE TABLE IF NOT EXISTS members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会员ID',
    card_number VARCHAR(20) NOT NULL UNIQUE COMMENT '会员卡号',
    name VARCHAR(50) NOT NULL COMMENT '会员姓名',
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '手机号码',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    level VARCHAR(20) NOT NULL DEFAULT 'REGULAR' COMMENT '会员等级',
    points INT NOT NULL DEFAULT 0 COMMENT '积分余额',
    stored_balance DOUBLE NOT NULL DEFAULT 0 COMMENT '储值余额',
    total_consumption DOUBLE NOT NULL DEFAULT 0 COMMENT '累计消费金额',
    status INT NOT NULL DEFAULT 1 COMMENT '会员状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_card_number (card_number),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- 交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易ID',
    transaction_no VARCHAR(32) NOT NULL UNIQUE COMMENT '交易单号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type VARCHAR(30) NOT NULL COMMENT '交易类型',
    amount DOUBLE NOT NULL COMMENT '交易金额',
    points_change INT NOT NULL DEFAULT 0 COMMENT '积分变动',
    balance_change DOUBLE NOT NULL DEFAULT 0 COMMENT '储值余额变动',
    points_after INT NOT NULL COMMENT '交易后积分余额',
    balance_after DOUBLE NOT NULL COMMENT '交易后储值余额',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    INDEX idx_member_id (member_id),
    INDEX idx_type (type),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';

-- 插入测试数据（可选）
-- 密码：123456（BCrypt加密后）
INSERT INTO members (card_number, name, phone, password, level, points, stored_balance, status) VALUES
('VIP20240101000001', '张三', '13800138001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'REGULAR', 100, 500.0, 1),
('VIP20240101000002', '李四', '13800138002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'SILVER', 500, 2500.0, 1),
('VIP20240101000003', '王五', '13800138003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'GOLD', 2000, 8000.0, 1);

-- =============================================
-- 超市会员管理系统 - MySQL部署脚本
-- 版本: 1.0.0
-- 创建日期: 2026-03-27
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket_member 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE supermarket_member;

-- =============================================
-- 1. 会员表
-- =============================================
CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    member_no VARCHAR(50) NOT NULL COMMENT '会员号',
    name VARCHAR(100) NOT NULL COMMENT '会员姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(200) COMMENT '邮箱',
    level VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '会员等级: NORMAL-普通, SILVER-银卡, GOLD-金卡',
    total_consumption DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
    points INT NOT NULL DEFAULT 0 COMMENT '当前积分',
    stored_balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '储值余额',
    password VARCHAR(255) COMMENT '密码',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_no (member_no),
    UNIQUE KEY uk_phone (phone),
    KEY idx_level (level),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员信息表';

-- =============================================
-- 2. 储值卡表
-- =============================================
CREATE TABLE IF NOT EXISTS stored_value_cards (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '储值卡ID',
    card_no VARCHAR(50) NOT NULL COMMENT '储值卡号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '卡内余额',
    active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活: 0-停用, 1-激活',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_card_no (card_no),
    KEY idx_member_id (member_id),
    KEY idx_active (active),
    CONSTRAINT fk_card_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='储值卡表';

-- =============================================
-- 3. 交易记录表
-- =============================================
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    transaction_no VARCHAR(50) NOT NULL COMMENT '交易流水号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type VARCHAR(20) NOT NULL COMMENT '交易类型: CONSUMPTION-消费, RECHARGE-充值, POINTS_EARN-积分获取, POINTS_USE-积分抵扣, GIFT-赠品',
    amount DECIMAL(10, 2) NOT NULL COMMENT '交易金额',
    points_earned DECIMAL(10, 2) COMMENT '获得积分',
    points_used DECIMAL(10, 2) COMMENT '使用积分',
    description VARCHAR(500) COMMENT '交易描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_member_id (member_id),
    KEY idx_type (type),
    KEY idx_created_at (created_at),
    CONSTRAINT fk_transaction_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';

-- =============================================
-- 4. 充值记录表
-- =============================================
CREATE TABLE IF NOT EXISTS recharge_records (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '充值记录ID',
    record_no VARCHAR(50) NOT NULL COMMENT '充值流水号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    card_id BIGINT NOT NULL COMMENT '储值卡ID',
    amount DECIMAL(10, 2) NOT NULL COMMENT '充值金额',
    description VARCHAR(500) COMMENT '充值描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_record_no (record_no),
    KEY idx_member_id (member_id),
    KEY idx_card_id (card_id),
    KEY idx_created_at (created_at),
    CONSTRAINT fk_recharge_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
    CONSTRAINT fk_recharge_card FOREIGN KEY (card_id) REFERENCES stored_value_cards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充值记录表';

-- =============================================
-- 5. 初始化数据
-- =============================================

-- 插入测试会员数据
INSERT INTO members (member_no, name, phone, email, level, total_consumption, points, stored_balance, password) VALUES
('M001', '张三', '13800138000', 'zhangsan@example.com', 'NORMAL', 0.00, 0, 0.00, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'),
('M002', '李四', '13800138001', 'lisi@example.com', 'SILVER', 1500.00, 150, 500.00, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi'),
('M003', '王五', '13800138002', 'wangwu@example.com', 'GOLD', 6000.00, 600, 1000.00, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');

-- 插入储值卡数据
INSERT INTO stored_value_cards (card_no, member_id, balance, active) VALUES
('CARD20260327001', 1, 0.00, 1),
('CARD20260327002', 2, 500.00, 1),
('CARD20260327003', 3, 1000.00, 1);

-- 插入交易记录数据
INSERT INTO transactions (transaction_no, member_id, type, amount, points_earned, points_used, description) VALUES
('TXN20260327001', 2, 'RECHARGE', 500.00, NULL, NULL, '储值卡充值'),
('TXN20260327002', 3, 'RECHARGE', 1000.00, NULL, NULL, '储值卡充值'),
('TXN20260327003', 2, 'CONSUMPTION', 100.00, 150.00, 0.00, '购买商品'),
('TXN20260327004', 3, 'CONSUMPTION', 200.00, 400.00, 0.00, '购买商品');

-- 插入充值记录数据
INSERT INTO recharge_records (record_no, member_id, card_id, amount, description) VALUES
('REC20260327001', 2, 2, 500.00, '储值卡充值'),
('REC20260327002', 3, 3, 1000.00, '储值卡充值');

-- =============================================
-- 6. 创建视图
-- =============================================

-- 会员统计视图
CREATE OR REPLACE VIEW v_member_statistics AS
SELECT 
    m.id,
    m.member_no,
    m.name,
    m.phone,
    m.level,
    m.total_consumption,
    m.points,
    m.stored_balance,
    COUNT(DISTINCT t.id) AS transaction_count,
    COUNT(DISTINCT c.id) AS card_count,
    m.created_at,
    m.updated_at
FROM members m
LEFT JOIN transactions t ON m.id = t.member_id
LEFT JOIN stored_value_cards c ON m.id = c.member_id
GROUP BY m.id, m.member_no, m.name, m.phone, m.level, m.total_consumption, m.points, m.stored_balance, m.created_at, m.updated_at;

-- 会员等级统计视图
CREATE OR REPLACE VIEW v_level_statistics AS
SELECT 
    level,
    COUNT(*) AS member_count,
    SUM(total_consumption) AS total_consumption_sum,
    AVG(total_consumption) AS avg_consumption,
    SUM(points) AS total_points,
    SUM(stored_balance) AS total_stored_balance
FROM members
GROUP BY level;

-- =============================================
-- 7. 创建存储过程
-- =============================================

DELIMITER //

-- 会员等级自动升级存储过程
CREATE PROCEDURE sp_update_member_level(IN p_member_id BIGINT)
BEGIN
    DECLARE v_total_consumption DECIMAL(10, 2);
    DECLARE v_new_level VARCHAR(20);
    
    -- 获取会员累计消费
    SELECT total_consumption INTO v_total_consumption
    FROM members WHERE id = p_member_id;
    
    -- 判断并更新会员等级
    IF v_total_consumption >= 5000.00 THEN
        SET v_new_level = 'GOLD';
    ELSEIF v_total_consumption >= 1000.00 THEN
        SET v_new_level = 'SILVER';
    ELSE
        SET v_new_level = 'NORMAL';
    END IF;
    
    -- 更新会员等级
    UPDATE members SET level = v_new_level WHERE id = p_member_id;
    
    SELECT v_new_level AS new_level;
END //

-- 消费处理存储过程
CREATE PROCEDURE sp_process_consumption(
    IN p_member_id BIGINT,
    IN p_amount DECIMAL(10, 2),
    IN p_points_to_use INT,
    OUT p_transaction_no VARCHAR(50),
    OUT p_points_earned INT,
    OUT p_final_amount DECIMAL(10, 2)
)
BEGIN
    DECLARE v_level VARCHAR(20);
    DECLARE v_discount DECIMAL(3, 2);
    DECLARE v_point_rate INT;
    DECLARE v_current_points INT;
    DECLARE v_discounted_amount DECIMAL(10, 2);
    DECLARE v_points_deduction DECIMAL(10, 2);
    
    -- 获取会员信息
    SELECT level, points INTO v_level, v_current_points
    FROM members WHERE id = p_member_id;
    
    -- 根据等级获取折扣率和积分倍率
    CASE v_level
        WHEN 'GOLD' THEN 
            SET v_discount = 0.90;
            SET v_point_rate = 2;
        WHEN 'SILVER' THEN
            SET v_discount = 0.95;
            SET v_point_rate = 1.5;
        ELSE
            SET v_discount = 1.00;
            SET v_point_rate = 1;
    END CASE;
    
    -- 计算折扣后金额
    SET v_discounted_amount = p_amount * v_discount;
    
    -- 检查积分是否足够
    IF p_points_to_use > v_current_points THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '积分不足';
    END IF;
    
    -- 计算积分抵扣
    SET v_points_deduction = p_points_to_use / 100.00;
    SET p_final_amount = v_discounted_amount - v_points_deduction;
    
    IF p_final_amount < 0 THEN
        SET p_final_amount = 0;
    END IF;
    
    -- 计算获得积分
    SET p_points_earned = FLOOR(p_final_amount * v_point_rate);
    
    -- 生成交易流水号
    SET p_transaction_no = CONCAT('TXN', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), SUBSTRING(MD5(RAND()), 1, 8));
    
    -- 插入交易记录
    INSERT INTO transactions (transaction_no, member_id, type, amount, points_earned, points_used, description)
    VALUES (p_transaction_no, p_member_id, 'CONSUMPTION', p_final_amount, p_points_earned, p_points_to_use, '消费');
    
    -- 更新会员信息
    UPDATE members 
    SET total_consumption = total_consumption + p_final_amount,
        points = points - p_points_to_use + p_points_earned
    WHERE id = p_member_id;
    
    -- 调用升级存储过程
    CALL sp_update_member_level(p_member_id);
END //

-- 充值处理存储过程
CREATE PROCEDURE sp_process_recharge(
    IN p_member_id BIGINT,
    IN p_amount DECIMAL(10, 2),
    OUT p_transaction_no VARCHAR(50),
    OUT p_card_no VARCHAR(50)
)
BEGIN
    DECLARE v_card_id BIGINT;
    DECLARE v_card_exists INT;
    
    -- 检查会员是否有储值卡
    SELECT COUNT(*), IFNULL(MAX(id), 0) INTO v_card_exists, v_card_id
    FROM stored_value_cards 
    WHERE member_id = p_member_id AND active = 1;
    
    -- 如果没有储值卡，创建一张
    IF v_card_exists = 0 THEN
        SET p_card_no = CONCAT('CARD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), SUBSTRING(MD5(RAND()), 1, 8));
        INSERT INTO stored_value_cards (card_no, member_id, balance, active)
        VALUES (p_card_no, p_member_id, 0.00, 1);
        SET v_card_id = LAST_INSERT_ID();
    ELSE
        SELECT card_no INTO p_card_no FROM stored_value_cards WHERE id = v_card_id;
    END IF;
    
    -- 更新储值卡余额
    UPDATE stored_value_cards 
    SET balance = balance + p_amount
    WHERE id = v_card_id;
    
    -- 生成交易流水号
    SET p_transaction_no = CONCAT('TXN', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), SUBSTRING(MD5(RAND()), 1, 8));
    
    -- 插入充值记录
    INSERT INTO recharge_records (record_no, member_id, card_id, amount, description)
    VALUES (CONCAT('REC', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), SUBSTRING(MD5(RAND()), 1, 8)), 
            p_member_id, v_card_id, p_amount, '储值卡充值');
    
    -- 插入交易记录
    INSERT INTO transactions (transaction_no, member_id, type, amount, description)
    VALUES (p_transaction_no, p_member_id, 'RECHARGE', p_amount, '储值卡充值');
    
    -- 更新会员信息
    UPDATE members 
    SET total_consumption = total_consumption + p_amount,
        stored_balance = stored_balance + p_amount
    WHERE id = p_member_id;
    
    -- 调用升级存储过程
    CALL sp_update_member_level(p_member_id);
END //

DELIMITER ;

-- =============================================
-- 8. 创建触发器
-- =============================================

DELIMITER //

-- 交易记录插入后自动更新会员统计
CREATE TRIGGER tr_after_transaction_insert
AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    -- 更新会员的累计消费和积分（如果还没有更新）
    -- 这里主要用于数据一致性检查，实际业务逻辑在存储过程中处理
    NULL;
END //

DELIMITER ;

-- =============================================
-- 9. 创建索引优化
-- =============================================

-- 为常用查询创建复合索引
CREATE INDEX idx_member_level_consumption ON members(level, total_consumption);
CREATE INDEX idx_transaction_member_type ON transactions(member_id, type);
CREATE INDEX idx_transaction_member_date ON transactions(member_id, created_at);

-- =============================================
-- 10. 授权
-- =============================================

-- 创建应用用户（根据实际情况修改）
-- CREATE USER 'supermarket_app'@'%' IDENTIFIED BY 'your_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON supermarket_member.* TO 'supermarket_app'@'%';
-- FLUSH PRIVILEGES;

-- =============================================
-- 完成
-- =============================================
SELECT '数据库部署完成！' AS message;

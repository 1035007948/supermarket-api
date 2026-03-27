-- =============================================
-- 超市管理后台API数据库部署脚本
-- 数据库: MySQL 8.0+
-- 作者: Supermarket API Team
-- 版本: 1.0.0
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS supermarket_db 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE supermarket_db;

-- =============================================
-- 用户表 (users)
-- =============================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 商品表 (products)
-- =============================================
DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '价格',
    stock INT NOT NULL COMMENT '库存',
    specification VARCHAR(100) COMMENT '规格',
    category VARCHAR(20) NOT NULL COMMENT '分类: FOOD/CLOTHING/HOME/DIGITAL/BEAUTY',
    status VARCHAR(20) NOT NULL COMMENT '状态: ON_SALE/OFF_SHELF/OUT_OF_STOCK/PRE_SALE',
    description TEXT COMMENT '描述',
    image_url VARCHAR(500) COMMENT '图片URL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- =============================================
-- 会员表 (members)
-- =============================================
DROP TABLE IF EXISTS members;
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    level VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '等级: NORMAL/SILVER/GOLD',
    points INT NOT NULL DEFAULT 0 COMMENT '积分',
    total_spent DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费',
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- =============================================
-- 会员交易记录表 (member_transactions)
-- =============================================
DROP TABLE IF EXISTS member_transactions;
CREATE TABLE member_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    type VARCHAR(20) NOT NULL COMMENT '类型: RECHARGE/CONSUME/REFUND/POINTS_EARN/POINTS_USE/DISCOUNT',
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额',
    points INT COMMENT '积分变动',
    description VARCHAR(500) COMMENT '描述',
    related_order_id BIGINT COMMENT '关联订单ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_member_id (member_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员交易记录表';

-- =============================================
-- 订单表 (orders)
-- =============================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式: ONLINE_PAYMENT/IN_STORE_PAYMENT',
    status VARCHAR(20) NOT NULL COMMENT '订单状态',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT '优惠金额',
    paid_amount DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    points_used INT DEFAULT 0 COMMENT '使用积分',
    points_earned INT DEFAULT 0 COMMENT '获得积分',
    receiver_name VARCHAR(50) COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) COMMENT '收货人电话',
    receiver_address VARCHAR(500) COMMENT '收货地址',
    delivery_type VARCHAR(20) COMMENT '配送方式: PICKUP/DELIVERY',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    prepared_at TIMESTAMP NULL COMMENT '备货完成时间',
    delivered_at TIMESTAMP NULL COMMENT '发货时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    remark VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_member_id (member_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (member_id) REFERENCES members(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- =============================================
-- 订单项表 (order_items)
-- =============================================
DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10, 2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小计',
    specification VARCHAR(100) COMMENT '规格',
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入管理员用户 (密码: admin123)
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'MANAGER');

-- 插入测试商品
INSERT INTO products (name, price, stock, specification, category, status, description) VALUES
('有机苹果', 15.80, 200, '500g/袋', 'FOOD', 'ON_SALE', '新鲜有机苹果，口感脆甜'),
('进口牛奶', 28.50, 150, '1L/盒', 'FOOD', 'ON_SALE', '进口纯牛奶，营养丰富'),
('男士T恤', 99.00, 80, 'M/L/XL', 'CLOTHING', 'ON_SALE', '纯棉舒适T恤'),
('智能手表', 599.00, 50, '标准版', 'DIGITAL', 'ON_SALE', '多功能智能手表'),
('护肤套装', 299.00, 60, '3件套', 'BEAUTY', 'ON_SALE', '保湿护肤套装'),
('台灯', 89.00, 100, 'LED护眼', 'HOME', 'ON_SALE', 'LED护眼台灯');

-- 插入测试会员
INSERT INTO members (name, phone, email, level, points, total_spent, balance) VALUES
('张三', '13800138001', 'zhangsan@example.com', 'NORMAL', 100, 1000.00, 500.00),
('李四', '13800138002', 'lisi@example.com', 'SILVER', 500, 6000.00, 1000.00),
('王五', '13800138003', 'wangwu@example.com', 'GOLD', 1000, 12000.00, 2000.00);

-- =============================================
-- 创建视图
-- =============================================

-- 订单详情视图
CREATE OR REPLACE VIEW v_order_details AS
SELECT 
    o.id AS order_id,
    o.order_no,
    o.member_id,
    m.name AS member_name,
    m.phone AS member_phone,
    m.level AS member_level,
    o.payment_method,
    o.status,
    o.total_amount,
    o.discount_amount,
    o.paid_amount,
    o.points_used,
    o.points_earned,
    o.created_at AS order_created_at,
    o.completed_at,
    COUNT(oi.id) AS item_count,
    SUM(oi.quantity) AS total_quantity
FROM orders o
LEFT JOIN members m ON o.member_id = m.id
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id;

-- 商品销售统计视图
CREATE OR REPLACE VIEW v_product_sales AS
SELECT 
    p.id AS product_id,
    p.name AS product_name,
    p.category,
    p.price,
    p.stock,
    COALESCE(SUM(oi.quantity), 0) AS total_sold,
    COALESCE(SUM(oi.subtotal), 0) AS total_revenue
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status IN ('COMPLETED')
GROUP BY p.id;

-- =============================================
-- 存储过程
-- =============================================

-- 会员等级自动升级存储过程
DELIMITER //
CREATE PROCEDURE sp_update_member_level(IN p_member_id BIGINT)
BEGIN
    DECLARE v_total_spent DECIMAL(10, 2);
    DECLARE v_new_level VARCHAR(20);
    
    SELECT total_spent INTO v_total_spent
    FROM members WHERE id = p_member_id;
    
    IF v_total_spent >= 10000 THEN
        SET v_new_level = 'GOLD';
    ELSEIF v_total_spent >= 5000 THEN
        SET v_new_level = 'SILVER';
    ELSE
        SET v_new_level = 'NORMAL';
    END IF;
    
    UPDATE members SET level = v_new_level WHERE id = p_member_id;
END //
DELIMITER ;

-- 订单统计存储过程
DELIMITER //
CREATE PROCEDURE sp_order_statistics(IN p_start_date DATE, IN p_end_date DATE)
BEGIN
    SELECT 
        COUNT(*) AS total_orders,
        SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_orders,
        SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_orders,
        SUM(CASE WHEN status = 'PENDING_PAYMENT' THEN 1 ELSE 0 END) AS pending_orders,
        SUM(CASE WHEN status = 'COMPLETED' THEN paid_amount ELSE 0 END) AS total_revenue,
        AVG(CASE WHEN status = 'COMPLETED' THEN paid_amount ELSE NULL END) AS avg_order_amount
    FROM orders
    WHERE DATE(created_at) BETWEEN p_start_date AND p_end_date;
END //
DELIMITER ;

-- =============================================
-- 触发器
-- =============================================

-- 订单完成后自动增加积分
DELIMITER //
CREATE TRIGGER tr_order_complete_points
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        UPDATE members 
        SET points = points + FLOOR(NEW.paid_amount * 0.1)
        WHERE id = NEW.member_id;
        
        INSERT INTO member_transactions (member_id, type, amount, points, description, related_order_id)
        VALUES (NEW.member_id, 'POINTS_EARN', 0, FLOOR(NEW.paid_amount * 0.1), 
                '订单完成获得积分', NEW.id);
    END IF;
END //
DELIMITER ;

-- =============================================
-- 索引优化建议
-- =============================================

-- 为常用查询添加复合索引
ALTER TABLE orders ADD INDEX idx_member_status (member_id, status);
ALTER TABLE orders ADD INDEX idx_created_status (created_at, status);
ALTER TABLE products ADD INDEX idx_category_status (category, status);

-- =============================================
-- 数据备份脚本
-- =============================================

-- 备份命令示例 (在命令行执行):
-- mysqldump -u root -p supermarket_db > supermarket_db_backup_$(date +%Y%m%d).sql

-- 恢复命令示例:
-- mysql -u root -p supermarket_db < supermarket_db_backup_20240101.sql

-- =============================================
-- 数据清理脚本 (定期执行)
-- =============================================

-- 清理90天前的已取消订单
DELETE FROM orders 
WHERE status = 'CANCELLED' 
AND created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 清理180天前的交易记录
DELETE FROM member_transactions 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 180 DAY);

-- =============================================
-- 完成提示
-- =============================================
SELECT '数据库部署完成！' AS message;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS product_count FROM products;
SELECT COUNT(*) AS member_count FROM members;

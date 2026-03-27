-- =============================================
-- 超市管理后台API测试数据
-- 用于开发和测试环境
-- =============================================

USE supermarket_db;

-- 清空现有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE member_transactions;
TRUNCATE TABLE members;
TRUNCATE TABLE products;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 用户数据
-- =============================================
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'ADMIN'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'MANAGER'),
('staff1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'USER'),
('staff2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'USER');

-- =============================================
-- 商品数据
-- =============================================

-- 食品类
INSERT INTO products (name, price, stock, specification, category, status, description, image_url) VALUES
('有机苹果', 15.80, 200, '500g/袋', 'FOOD', 'ON_SALE', '新鲜有机苹果，口感脆甜', '/images/apple.jpg'),
('进口牛奶', 28.50, 150, '1L/盒', 'FOOD', 'ON_SALE', '进口纯牛奶，营养丰富', '/images/milk.jpg'),
('有机蔬菜', 12.00, 180, '500g/份', 'FOOD', 'ON_SALE', '新鲜有机蔬菜', '/images/vegetable.jpg'),
('进口牛肉', 68.00, 80, '500g/盒', 'FOOD', 'ON_SALE', '澳洲进口牛肉', '/images/beef.jpg'),
('海鲜大礼包', 188.00, 50, '2kg/盒', 'FOOD', 'PRE_SALE', '精选海鲜大礼包', '/images/seafood.jpg'),
('进口零食', 35.00, 0, '500g/袋', 'FOOD', 'OUT_OF_STOCK', '进口休闲零食', '/images/snacks.jpg');

-- 服饰类
INSERT INTO products (name, price, stock, specification, category, status, description, image_url) VALUES
('男士T恤', 99.00, 80, 'M/L/XL/XXL', 'CLOTHING', 'ON_SALE', '纯棉舒适T恤', '/images/tshirt.jpg'),
('女士连衣裙', 199.00, 60, 'S/M/L', 'CLOTHING', 'ON_SALE', '时尚优雅连衣裙', '/images/dress.jpg'),
('运动套装', 299.00, 40, 'M/L/XL', 'CLOTHING', 'ON_SALE', '透气运动套装', '/images/sportswear.jpg'),
('羽绒服', 599.00, 30, 'M/L/XL', 'CLOTHING', 'OFF_SHELF', '保暖羽绒服', '/images/jacket.jpg');

-- 家居类
INSERT INTO products (name, price, stock, specification, category, status, description, image_url) VALUES
('LED台灯', 89.00, 100, '标准版', 'HOME', 'ON_SALE', 'LED护眼台灯', '/images/lamp.jpg'),
('四件套床品', 399.00, 50, '1.8m床', 'HOME', 'ON_SALE', '纯棉四件套', '/images/bedding.jpg'),
('收纳盒套装', 49.00, 200, '3件套', 'HOME', 'ON_SALE', '多功能收纳盒', '/images/storage.jpg'),
('厨房刀具套装', 299.00, 70, '7件套', 'HOME', 'ON_SALE', '德国工艺刀具', '/images/knife.jpg');

-- 数码类
INSERT INTO products (name, price, stock, specification, category, status, description, image_url) VALUES
('智能手表', 599.00, 50, '标准版', 'DIGITAL', 'ON_SALE', '多功能智能手表', '/images/watch.jpg'),
('蓝牙耳机', 199.00, 100, '入耳式', 'DIGITAL', 'ON_SALE', '降噪蓝牙耳机', '/images/earphone.jpg'),
('移动电源', 89.00, 150, '20000mAh', 'DIGITAL', 'ON_SALE', '大容量移动电源', '/images/powerbank.jpg'),
('智能音箱', 299.00, 80, '标准版', 'DIGITAL', 'ON_SALE', 'AI智能音箱', '/images/speaker.jpg');

-- 美妆类
INSERT INTO products (name, price, stock, specification, category, status, description, image_url) VALUES
('护肤套装', 299.00, 60, '3件套', 'BEAUTY', 'ON_SALE', '保湿护肤套装', '/images/skincare.jpg'),
('口红套装', 199.00, 80, '6色', 'BEAUTY', 'ON_SALE', '大牌口红套装', '/images/lipstick.jpg'),
('面膜套装', 89.00, 120, '10片装', 'BEAUTY', 'ON_SALE', '补水面膜', '/images/mask.jpg'),
('香水', 399.00, 40, '50ml', 'BEAUTY', 'ON_SALE', '经典香水', '/images/perfume.jpg');

-- =============================================
-- 会员数据
-- =============================================
INSERT INTO members (name, phone, email, level, points, total_spent, balance) VALUES
('张三', '13800138001', 'zhangsan@example.com', 'NORMAL', 100, 1000.00, 500.00),
('李四', '13800138002', 'lisi@example.com', 'SILVER', 500, 6000.00, 1000.00),
('王五', '13800138003', 'wangwu@example.com', 'GOLD', 1000, 12000.00, 2000.00),
('赵六', '13800138004', 'zhaoliu@example.com', 'NORMAL', 50, 500.00, 200.00),
('孙七', '13800138005', 'sunqi@example.com', 'NORMAL', 200, 2000.00, 800.00);

-- =============================================
-- 会员交易记录
-- =============================================
INSERT INTO member_transactions (member_id, type, amount, points, description) VALUES
(1, 'RECHARGE', 500.00, 50, '储值卡充值'),
(1, 'CONSUME', 100.00, 10, '购买商品'),
(2, 'RECHARGE', 1000.00, 100, '储值卡充值'),
(2, 'POINTS_EARN', 0.00, 500, '消费累计积分'),
(3, 'RECHARGE', 2000.00, 200, '储值卡充值'),
(3, 'POINTS_EARN', 0.00, 1000, '消费累计积分');

-- =============================================
-- 订单数据
-- =============================================
INSERT INTO orders (order_no, member_id, payment_method, status, total_amount, discount_amount, paid_amount, points_used, points_earned, delivery_type, created_at, paid_at, prepared_at, completed_at) VALUES
('ORD202401010001', 1, 'ONLINE_PAYMENT', 'COMPLETED', 199.00, 0.00, 199.00, 0, 19, 'PICKUP', 
 DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('ORD202401020001', 2, 'IN_STORE_PAYMENT', 'COMPLETED', 599.00, 29.95, 569.05, 0, 56, 'DELIVERY',
 DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('ORD202401030001', 3, 'ONLINE_PAYMENT', 'COMPLETED', 299.00, 29.90, 269.10, 0, 26, 'PICKUP',
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD202401040001', 1, 'ONLINE_PAYMENT', 'PENDING_PAYMENT', 89.00, 0.00, 89.00, 0, 0, 'PICKUP',
 NOW(), NULL, NULL, NULL),
('ORD202401050001', 2, 'ONLINE_PAYMENT', 'PREPARING', 399.00, 19.95, 379.05, 0, 0, 'DELIVERY',
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL),
('ORD202401060001', 3, 'IN_STORE_PAYMENT', 'READY_FOR_PICKUP', 199.00, 19.90, 179.10, 0, 0, 'PICKUP',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL);

-- =============================================
-- 订单项数据
-- =============================================
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, subtotal, specification) VALUES
(1, 1, '有机苹果', 15.80, 5, 79.00, '500g/袋'),
(1, 2, '进口牛奶', 28.50, 4, 114.00, '1L/盒'),
(2, 11, '智能手表', 599.00, 1, 599.00, '标准版'),
(3, 15, '护肤套装', 299.00, 1, 299.00, '3件套'),
(4, 13, 'LED台灯', 89.00, 1, 89.00, '标准版'),
(5, 9, '四件套床品', 399.00, 1, 399.00, '1.8m床'),
(6, 1, '有机苹果', 15.80, 10, 158.00, '500g/袋'),
(6, 3, '有机蔬菜', 12.00, 2, 24.00, '500g/份'),
(6, 17, '面膜套装', 89.00, 1, 89.00, '10片装');

-- =============================================
-- 验证数据
-- =============================================
SELECT '用户数量' AS 统计项, COUNT(*) AS 数量 FROM users
UNION ALL
SELECT '商品数量', COUNT(*) FROM products
UNION ALL
SELECT '会员数量', COUNT(*) FROM members
UNION ALL
SELECT '订单数量', COUNT(*) FROM orders
UNION ALL
SELECT '订单项数量', COUNT(*) FROM order_items;

SELECT '测试数据初始化完成！' AS message;

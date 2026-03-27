-- =============================================
-- 超市会员管理系统 - 数据清理脚本
-- 用于开发和测试环境
-- =============================================

USE supermarket_member;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空所有表数据
TRUNCATE TABLE recharge_records;
TRUNCATE TABLE transactions;
TRUNCATE TABLE stored_value_cards;
TRUNCATE TABLE members;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '数据清理完成！' AS message;

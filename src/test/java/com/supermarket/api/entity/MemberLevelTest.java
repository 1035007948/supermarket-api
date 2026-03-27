package com.supermarket.api.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 会员等级枚举测试类
 */
class MemberLevelTest {

    @Test
    void testGetDescription() {
        assertEquals("普通会员", MemberLevel.REGULAR.getDescription());
        assertEquals("银卡会员", MemberLevel.SILVER.getDescription());
        assertEquals("金卡会员", MemberLevel.GOLD.getDescription());
    }

    @Test
    void testGetMinBalance() {
        assertEquals(0, MemberLevel.REGULAR.getMinBalance());
        assertEquals(1000, MemberLevel.SILVER.getMinBalance());
        assertEquals(5000, MemberLevel.GOLD.getMinBalance());
    }

    @Test
    void testGetDiscountRate() {
        assertEquals(0.0, MemberLevel.REGULAR.getDiscountRate(), 0.001);
        assertEquals(0.05, MemberLevel.SILVER.getDiscountRate(), 0.001);
        assertEquals(0.1, MemberLevel.GOLD.getDiscountRate(), 0.001);
    }

    @Test
    void testGetPointMultiplier() {
        assertEquals(1.0, MemberLevel.REGULAR.getPointMultiplier(), 0.001);
        assertEquals(1.2, MemberLevel.SILVER.getPointMultiplier(), 0.001);
        assertEquals(1.5, MemberLevel.GOLD.getPointMultiplier(), 0.001);
    }

    @Test
    void testCalculateLevel_Regular() {
        assertEquals(MemberLevel.REGULAR, MemberLevel.calculateLevel(0.0));
        assertEquals(MemberLevel.REGULAR, MemberLevel.calculateLevel(500.0));
        assertEquals(MemberLevel.REGULAR, MemberLevel.calculateLevel(999.99));
    }

    @Test
    void testCalculateLevel_Silver() {
        assertEquals(MemberLevel.SILVER, MemberLevel.calculateLevel(1000.0));
        assertEquals(MemberLevel.SILVER, MemberLevel.calculateLevel(2500.0));
        assertEquals(MemberLevel.SILVER, MemberLevel.calculateLevel(4999.99));
    }

    @Test
    void testCalculateLevel_Gold() {
        assertEquals(MemberLevel.GOLD, MemberLevel.calculateLevel(5000.0));
        assertEquals(MemberLevel.GOLD, MemberLevel.calculateLevel(10000.0));
        assertEquals(MemberLevel.GOLD, MemberLevel.calculateLevel(99999.99));
    }
}

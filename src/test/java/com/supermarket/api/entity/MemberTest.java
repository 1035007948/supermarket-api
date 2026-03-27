package com.supermarket.api.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 会员实体测试类
 */
class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("测试会员");
        member.setPhone("13800138000");
        member.setCardNumber("VIP20240101000001");
        member.setLevel(MemberLevel.REGULAR);
        member.setPoints(0);
        member.setStoredBalance(0.0);
        member.setStatus(1);
    }

    @Test
    void testUpdateLevelByBalance_RegularToSilver() {
        member.setStoredBalance(1000.0);
        member.updateLevelByBalance();
        assertEquals(MemberLevel.SILVER, member.getLevel());
    }

    @Test
    void testUpdateLevelByBalance_RegularToGold() {
        member.setStoredBalance(5000.0);
        member.updateLevelByBalance();
        assertEquals(MemberLevel.GOLD, member.getLevel());
    }

    @Test
    void testUpdateLevelByBalance_SilverToGold() {
        member.setLevel(MemberLevel.SILVER);
        member.setStoredBalance(5000.0);
        member.updateLevelByBalance();
        assertEquals(MemberLevel.GOLD, member.getLevel());
    }

    @Test
    void testUpdateLevelByBalance_GoldRemainsGold() {
        member.setLevel(MemberLevel.GOLD);
        member.setStoredBalance(10000.0);
        member.updateLevelByBalance();
        assertEquals(MemberLevel.GOLD, member.getLevel());
    }

    @Test
    void testCalculateDiscountedAmount_Regular() {
        member.setLevel(MemberLevel.REGULAR);
        double discounted = member.calculateDiscountedAmount(100.0);
        assertEquals(100.0, discounted, 0.001);
    }

    @Test
    void testCalculateDiscountedAmount_Silver() {
        member.setLevel(MemberLevel.SILVER);
        double discounted = member.calculateDiscountedAmount(100.0);
        assertEquals(95.0, discounted, 0.001);
    }

    @Test
    void testCalculateDiscountedAmount_Gold() {
        member.setLevel(MemberLevel.GOLD);
        double discounted = member.calculateDiscountedAmount(100.0);
        assertEquals(90.0, discounted, 0.001);
    }

    @Test
    void testCalculatePoints_Regular() {
        member.setLevel(MemberLevel.REGULAR);
        int points = member.calculatePoints(100.0);
        assertEquals(100, points);
    }

    @Test
    void testCalculatePoints_Silver() {
        member.setLevel(MemberLevel.SILVER);
        int points = member.calculatePoints(100.0);
        assertEquals(120, points);
    }

    @Test
    void testCalculatePoints_Gold() {
        member.setLevel(MemberLevel.GOLD);
        int points = member.calculatePoints(100.0);
        assertEquals(150, points);
    }

    @Test
    void testAddPoints() {
        member.addPoints(100);
        assertEquals(100, member.getPoints());

        member.addPoints(50);
        assertEquals(150, member.getPoints());
    }

    @Test
    void testUsePoints_Sufficient() {
        member.setPoints(100);
        boolean result = member.usePoints(50);
        assertTrue(result);
        assertEquals(50, member.getPoints());
    }

    @Test
    void testUsePoints_Insufficient() {
        member.setPoints(50);
        boolean result = member.usePoints(100);
        assertFalse(result);
        assertEquals(50, member.getPoints());
    }

    @Test
    void testRecharge() {
        member.recharge(1000.0);
        assertEquals(1000.0, member.getStoredBalance(), 0.001);
        assertEquals(MemberLevel.SILVER, member.getLevel());

        member.recharge(4000.0);
        assertEquals(5000.0, member.getStoredBalance(), 0.001);
        assertEquals(MemberLevel.GOLD, member.getLevel());
    }

    @Test
    void testUseStoredBalance_Sufficient() {
        member.setStoredBalance(1000.0);
        boolean result = member.useStoredBalance(500.0);
        assertTrue(result);
        assertEquals(500.0, member.getStoredBalance(), 0.001);
        assertEquals(500.0, member.getTotalConsumption(), 0.001);
    }

    @Test
    void testUseStoredBalance_Insufficient() {
        member.setStoredBalance(500.0);
        boolean result = member.useStoredBalance(1000.0);
        assertFalse(result);
        assertEquals(500.0, member.getStoredBalance(), 0.001);
        assertEquals(0.0, member.getTotalConsumption(), 0.001);
    }
}

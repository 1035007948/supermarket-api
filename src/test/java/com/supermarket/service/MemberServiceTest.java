package com.supermarket.service;

import com.supermarket.entity.Member;
import com.supermarket.entity.StoredValueCard;
import com.supermarket.enums.MemberLevel;
import com.supermarket.repository.MemberRepository;
import com.supermarket.repository.StoredValueCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setName("测试会员");
        testMember.setPhone("13800138000");
        testMember = memberService.createMember(testMember);
    }

    @Test
    void testCreateMember() {
        assertNotNull(testMember.getId());
        assertEquals(MemberLevel.REGULAR, testMember.getLevel());
        assertEquals(0, testMember.getPoints());
    }

    @Test
    void testRechargePointsAndLevelUpgrade() {
        StoredValueCard card = memberService.getStoredValueCardsByMemberId(testMember.getId()).get(0);
        assertNotNull(card);

        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("500"), "测试充值");

        Member updatedMember = memberService.getMemberById(testMember.getId());
        assertEquals(500, updatedMember.getPoints());
        assertEquals(MemberLevel.REGULAR, updatedMember.getLevel());

        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("500"), "测试充值2");

        updatedMember = memberService.getMemberById(testMember.getId());
        assertEquals(1000, updatedMember.getPoints());
        assertEquals(MemberLevel.SILVER, updatedMember.getLevel());

        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("4000"), "测试充值3");

        updatedMember = memberService.getMemberById(testMember.getId());
        assertEquals(5000, updatedMember.getPoints());
        assertEquals(MemberLevel.GOLD, updatedMember.getLevel());
    }

    @Test
    void testConsumeAddPoints() {
        memberService.addPoints(testMember.getId(), new BigDecimal("100"));

        Member updatedMember = memberService.getMemberById(testMember.getId());
        assertEquals(100, updatedMember.getPoints());
    }

    @Test
    void testCalculateDiscount() {
        var discountInfo = memberService.calculateDiscount(testMember.getId(), new BigDecimal("100"));
        assertEquals("普通", discountInfo.get("memberLevel"));
        assertEquals(1.0, discountInfo.get("discountRate"));

        StoredValueCard card = memberService.getStoredValueCardsByMemberId(testMember.getId()).get(0);
        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("1000"), "升级充值");

        discountInfo = memberService.calculateDiscount(testMember.getId(), new BigDecimal("100"));
        assertEquals("银卡", discountInfo.get("memberLevel"));
        assertEquals(0.95, discountInfo.get("discountRate"));
    }

    @Test
    void testDeductPoints() {
        StoredValueCard card = memberService.getStoredValueCardsByMemberId(testMember.getId()).get(0);
        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("200"), "测试充值");

        Member updatedMember = memberService.deductPoints(testMember.getId(), 100);
        assertEquals(100, updatedMember.getPoints());
    }

    @Test
    void testDeductPointsInsufficient() {
        assertThrows(IllegalStateException.class, () -> {
            memberService.deductPoints(testMember.getId(), 100);
        });
    }

    @Test
    void testStoredValueCardConsume() {
        StoredValueCard card = memberService.getStoredValueCardsByMemberId(testMember.getId()).get(0);
        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("1000"), "测试充值");

        StoredValueCard updatedCard = memberService.consumeStoredValueCard(
                card.getId(), new BigDecimal("200"), null, "消费测试");

        assertEquals(new BigDecimal("800.00"), updatedCard.getBalance());
        assertEquals(new BigDecimal("200.00"), updatedCard.getTotalConsumption());
    }

    @Test
    void testStoredValueCardConsumeInsufficientBalance() {
        StoredValueCard card = memberService.getStoredValueCardsByMemberId(testMember.getId()).get(0);
        memberService.rechargeStoredValueCard(card.getId(), new BigDecimal("100"), "测试充值");

        assertThrows(IllegalStateException.class, () -> {
            memberService.consumeStoredValueCard(card.getId(), new BigDecimal("200"), null, "消费测试");
        });
    }
}
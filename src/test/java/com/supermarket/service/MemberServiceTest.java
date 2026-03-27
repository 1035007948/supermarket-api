package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.MemberRequest;
import com.supermarket.dto.MemberResponse;
import com.supermarket.dto.RechargeRequest;
import com.supermarket.entity.MemberLevel;
import com.supermarket.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        memberRequest = new MemberRequest();
        memberRequest.setName("张三");
        memberRequest.setPhone("13800138001");
        memberRequest.setEmail("zhangsan@test.com");
    }

    @Test
    @DisplayName("创建会员-成功")
    void createMember_Success() {
        MemberResponse response = memberService.createMember(memberRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("张三", response.getName());
        assertEquals("13800138001", response.getPhone());
        assertEquals(MemberLevel.NORMAL, response.getLevel());
        assertEquals(0, response.getPoints());
        assertEquals(BigDecimal.ZERO, response.getTotalSpent());
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    @DisplayName("创建会员-手机号重复")
    void createMember_DuplicatePhone() {
        memberService.createMember(memberRequest);

        MemberRequest duplicateRequest = new MemberRequest();
        duplicateRequest.setName("李四");
        duplicateRequest.setPhone("13800138001");

        assertThrows(BusinessException.class, () -> {
            memberService.createMember(duplicateRequest);
        });
    }

    @Test
    @DisplayName("获取会员详情-成功")
    void getMemberById_Success() {
        MemberResponse created = memberService.createMember(memberRequest);
        MemberResponse response = memberService.getMemberById(created.getId());

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
        assertEquals("张三", response.getName());
    }

    @Test
    @DisplayName("获取会员详情-会员不存在")
    void getMemberById_NotFound() {
        assertThrows(BusinessException.class, () -> {
            memberService.getMemberById(999L);
        });
    }

    @Test
    @DisplayName("按手机号查询会员")
    void getMemberByPhone() {
        memberService.createMember(memberRequest);
        MemberResponse response = memberService.getMemberByPhone("13800138001");

        assertNotNull(response);
        assertEquals("张三", response.getName());
    }

    @Test
    @DisplayName("充值-获得积分和赠送金额")
    void recharge_WithBonusAndPoints() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("500"));

        MemberResponse response = memberService.recharge(created.getId(), rechargeRequest);

        BigDecimal expectedBonus = new BigDecimal("50");
        BigDecimal expectedTotal = new BigDecimal("550");
        assertEquals(expectedTotal, response.getBalance());
        assertEquals(50, response.getPoints());
    }

    @Test
    @DisplayName("充值-小金额无赠送")
    void recharge_NoBonus() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("100"));

        MemberResponse response = memberService.recharge(created.getId(), rechargeRequest);

        assertEquals(new BigDecimal("100"), response.getBalance());
        assertEquals(10, response.getPoints());
    }

    @Test
    @DisplayName("充值累计消费-升级银卡会员")
    void recharge_UpgradeToSilver() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("5000"));

        MemberResponse response = memberService.recharge(created.getId(), rechargeRequest);

        assertEquals(MemberLevel.SILVER, response.getLevel());
    }

    @Test
    @DisplayName("充值累计消费-升级金卡会员")
    void recharge_UpgradeToGold() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("10000"));

        MemberResponse response = memberService.recharge(created.getId(), rechargeRequest);

        assertEquals(MemberLevel.GOLD, response.getLevel());
    }

    @Test
    @DisplayName("增加积分-成功")
    void addPoints_Success() {
        MemberResponse created = memberService.createMember(memberRequest);

        MemberResponse response = memberService.addPoints(created.getId(), 100);

        assertEquals(100, response.getPoints());
    }

    @Test
    @DisplayName("使用积分-成功")
    void usePoints_Success() {
        MemberResponse created = memberService.createMember(memberRequest);
        memberService.addPoints(created.getId(), 100);

        MemberResponse response = memberService.usePoints(created.getId(), 50);

        assertEquals(50, response.getPoints());
    }

    @Test
    @DisplayName("使用积分-积分不足")
    void usePoints_InsufficientPoints() {
        MemberResponse created = memberService.createMember(memberRequest);

        assertThrows(BusinessException.class, () -> {
            memberService.usePoints(created.getId(), 100);
        });
    }

    @Test
    @DisplayName("更新会员信息-成功")
    void updateMember_Success() {
        MemberResponse created = memberService.createMember(memberRequest);

        MemberRequest updateRequest = new MemberRequest();
        updateRequest.setName("李四");
        updateRequest.setPhone("13800138002");
        updateRequest.setEmail("lisi@test.com");

        MemberResponse response = memberService.updateMember(created.getId(), updateRequest);

        assertEquals("李四", response.getName());
        assertEquals("13800138002", response.getPhone());
        assertEquals("lisi@test.com", response.getEmail());
    }

    @Test
    @DisplayName("删除会员-成功")
    void deleteMember_Success() {
        MemberResponse created = memberService.createMember(memberRequest);

        memberService.deleteMember(created.getId());

        assertFalse(memberRepository.existsById(created.getId()));
    }

    @Test
    @DisplayName("按等级查询会员")
    void getMembersByLevel() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("5000"));
        memberService.recharge(created.getId(), rechargeRequest);

        Page<MemberResponse> page = memberService.getMembersByLevel(
                MemberLevel.SILVER, 
                PageRequest.of(0, 10)
        );

        assertTrue(page.getTotalElements() > 0);
        page.getContent().forEach(m -> assertEquals(MemberLevel.SILVER, m.getLevel()));
    }

    @Test
    @DisplayName("查询交易记录")
    void getMemberTransactions() {
        MemberResponse created = memberService.createMember(memberRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("100"));
        memberService.recharge(created.getId(), rechargeRequest);

        var transactions = memberService.getMemberTransactions(created.getId(), PageRequest.of(0, 10));

        assertTrue(transactions.getTotalElements() > 0);
    }
}

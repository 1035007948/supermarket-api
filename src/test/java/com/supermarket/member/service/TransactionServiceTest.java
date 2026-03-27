package com.supermarket.member.service;

import com.supermarket.member.dto.ConsumptionRequest;
import com.supermarket.member.dto.MemberRegisterRequest;
import com.supermarket.member.dto.RechargeRequest;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.Transaction;
import com.supermarket.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        MemberRegisterRequest registerRequest = new MemberRegisterRequest();
        registerRequest.setMemberNo("TEST002");
        registerRequest.setName("交易测试会员");
        registerRequest.setPhone("13800138001");
        registerRequest.setPassword("password123");

        testMember = memberService.register(registerRequest);
    }

    @Test
    void testConsume() {
        ConsumptionRequest request = new ConsumptionRequest();
        request.setMemberId(testMember.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("测试消费");

        Transaction transaction = transactionService.consume(request);

        assertNotNull(transaction.getId());
        assertEquals(0, new BigDecimal("100.00").compareTo(transaction.getAmount()));
        assertNotNull(transaction.getTransactionNo());

        Member updatedMember = memberRepository.findById(testMember.getId()).orElse(null);
        assertNotNull(updatedMember);
        assertTrue(updatedMember.getPoints() > 0);
    }

    @Test
    void testConsumeWithPoints() {
        testMember.setPoints(100);
        memberRepository.save(testMember);

        ConsumptionRequest request = new ConsumptionRequest();
        request.setMemberId(testMember.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setPointsToUse(50);
        request.setDescription("积分抵扣消费");

        Transaction transaction = transactionService.consume(request);

        assertNotNull(transaction);
        assertEquals(new BigDecimal("50"), transaction.getPointsUsed());

        Member updatedMember = memberRepository.findById(testMember.getId()).orElse(null);
        assertNotNull(updatedMember);
        assertEquals(50, updatedMember.getPoints() - transaction.getPointsEarned().intValue());
    }

    @Test
    void testRecharge() {
        RechargeRequest request = new RechargeRequest();
        request.setMemberId(testMember.getId());
        request.setAmount(new BigDecimal("500.00"));
        request.setDescription("测试充值");

        Transaction transaction = transactionService.recharge(request);

        assertNotNull(transaction.getId());
        assertEquals(0, new BigDecimal("500.00").compareTo(transaction.getAmount()));
        assertNotNull(transaction.getTransactionNo());

        Member updatedMember = memberRepository.findById(testMember.getId()).orElse(null);
        assertNotNull(updatedMember);
        assertEquals(0, new BigDecimal("500.00").compareTo(updatedMember.getStoredBalance()));
    }

    @Test
    void testGetMemberTransactions() {
        ConsumptionRequest consumeRequest = new ConsumptionRequest();
        consumeRequest.setMemberId(testMember.getId());
        consumeRequest.setAmount(new BigDecimal("100.00"));
        transactionService.consume(consumeRequest);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setMemberId(testMember.getId());
        rechargeRequest.setAmount(new BigDecimal("200.00"));
        transactionService.recharge(rechargeRequest);

        List<Transaction> transactions = transactionService.getMemberTransactions(testMember.getId());

        assertEquals(2, transactions.size());
    }
}

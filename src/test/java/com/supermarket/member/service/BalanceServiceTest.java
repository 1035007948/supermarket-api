package com.supermarket.member.service;

import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.BalanceRecord;
import com.supermarket.member.entity.Member;
import com.supermarket.member.enums.BalanceTypeEnum;
import com.supermarket.member.mapper.BalanceRecordMapper;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.service.impl.BalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRecordMapper balanceRecordMapper;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testRecharge() {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("200.00"));
        rechargeDTO.setPaymentMethod("支付宝");
        rechargeDTO.setDescription("会员充值");

        when(memberMapper.selectById(1L)).thenReturn(member);
        when(balanceRecordMapper.insert(any(BalanceRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> balanceService.recharge(rechargeDTO));

        verify(balanceRecordMapper, times(1)).insert(any(BalanceRecord.class));
    }

    @Test
    void testRecharge_MemberNotFound() {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("200.00"));
        rechargeDTO.setPaymentMethod("支付宝");

        when(memberMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            balanceService.recharge(rechargeDTO);
        });

        assertEquals("会员不存在", exception.getMessage());
    }

    @Test
    void testConsume() {
        when(memberMapper.selectById(1L)).thenReturn(member);
        when(balanceRecordMapper.insert(any(BalanceRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> balanceService.consume(1L, new BigDecimal("50.00"), "ORDER001", "购买商品"));

        verify(balanceRecordMapper, times(1)).insert(any(BalanceRecord.class));
    }

    @Test
    void testConsume_InsufficientBalance() {
        member.setBalance(new BigDecimal("30.00"));
        when(memberMapper.selectById(1L)).thenReturn(member);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            balanceService.consume(1L, new BigDecimal("50.00"), "ORDER001", "购买商品");
        });

        assertEquals("余额不足", exception.getMessage());
    }

    @Test
    void testRefund() {
        when(memberMapper.selectById(1L)).thenReturn(member);
        when(balanceRecordMapper.insert(any(BalanceRecord.class))).thenReturn(1);
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        assertDoesNotThrow(() -> balanceService.refund(1L, new BigDecimal("30.00"), "ORDER001", "订单退款"));

        verify(balanceRecordMapper, times(1)).insert(any(BalanceRecord.class));
        verify(memberMapper, times(1)).updateById(any(Member.class));
    }

    @Test
    void testGetMemberBalanceRecords() {
        BalanceRecord record1 = new BalanceRecord();
        record1.setId(1L);
        record1.setMemberId(1L);
        record1.setAmount(new BigDecimal("100.00"));
        record1.setType(BalanceTypeEnum.RECHARGE.getCode());

        BalanceRecord record2 = new BalanceRecord();
        record2.setId(2L);
        record2.setMemberId(1L);
        record2.setAmount(new BigDecimal("-50.00"));
        record2.setType(BalanceTypeEnum.CONSUME.getCode());

        when(balanceRecordMapper.selectByMemberId(1L)).thenReturn(Arrays.asList(record1, record2));

        List<BalanceRecord> result = balanceService.getMemberBalanceRecords(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getAmount().compareTo(new BigDecimal("100.00")));
        assertEquals(0, result.get(1).getAmount().compareTo(new BigDecimal("-50.00")));
    }
}

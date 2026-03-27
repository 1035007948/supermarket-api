package com.supermarket.member.service;

import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.BalanceRecord;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {

    void recharge(RechargeDTO rechargeDTO);

    void consume(Long memberId, BigDecimal amount, String orderNo, String description);

    void refund(Long memberId, BigDecimal amount, String orderNo, String description);

    List<BalanceRecord> getMemberBalanceRecords(Long memberId);
}

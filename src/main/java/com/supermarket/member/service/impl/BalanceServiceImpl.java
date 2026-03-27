package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.BalanceRecord;
import com.supermarket.member.entity.Member;
import com.supermarket.member.enums.BalanceTypeEnum;
import com.supermarket.member.mapper.BalanceRecordMapper;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BalanceServiceImpl extends ServiceImpl<BalanceRecordMapper, BalanceRecord> implements BalanceService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    @Transactional
    public void recharge(RechargeDTO rechargeDTO) {
        Member member = memberMapper.selectById(rechargeDTO.getMemberId());
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        BalanceRecord record = new BalanceRecord();
        record.setMemberId(rechargeDTO.getMemberId());
        record.setType(BalanceTypeEnum.RECHARGE.getCode());
        record.setAmount(rechargeDTO.getAmount());
        record.setBeforeBalance(member.getBalance());
        record.setAfterBalance(member.getBalance().add(rechargeDTO.getAmount()));
        record.setSource("充值");
        record.setPaymentMethod(rechargeDTO.getPaymentMethod());
        record.setDescription(rechargeDTO.getDescription());

        baseMapper.insert(record);
    }

    @Override
    @Transactional
    public void consume(Long memberId, BigDecimal amount, String orderNo, String description) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        if (member.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }

        BalanceRecord record = new BalanceRecord();
        record.setMemberId(memberId);
        record.setType(BalanceTypeEnum.CONSUME.getCode());
        record.setAmount(amount.negate());
        record.setBeforeBalance(member.getBalance());
        record.setAfterBalance(member.getBalance().subtract(amount));
        record.setSource("消费");
        record.setOrderNo(orderNo);
        record.setDescription(description);

        baseMapper.insert(record);
    }

    @Override
    @Transactional
    public void refund(Long memberId, BigDecimal amount, String orderNo, String description) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        BalanceRecord record = new BalanceRecord();
        record.setMemberId(memberId);
        record.setType(BalanceTypeEnum.REFUND.getCode());
        record.setAmount(amount);
        record.setBeforeBalance(member.getBalance());
        record.setAfterBalance(member.getBalance().add(amount));
        record.setSource("退款");
        record.setOrderNo(orderNo);
        record.setDescription(description);

        baseMapper.insert(record);

        member.setBalance(member.getBalance().add(amount));
        memberMapper.updateById(member);
    }

    @Override
    public List<BalanceRecord> getMemberBalanceRecords(Long memberId) {
        return baseMapper.selectByMemberId(memberId);
    }
}

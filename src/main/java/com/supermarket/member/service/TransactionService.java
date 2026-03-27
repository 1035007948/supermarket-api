package com.supermarket.member.service;

import com.supermarket.member.dto.ConsumptionRequest;
import com.supermarket.member.dto.RechargeRequest;
import com.supermarket.member.entity.*;
import com.supermarket.member.repository.MemberRepository;
import com.supermarket.member.repository.RechargeRecordRepository;
import com.supermarket.member.repository.StoredValueCardRepository;
import com.supermarket.member.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    @Autowired
    private MemberService memberService;

    public Transaction consume(ConsumptionRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        BigDecimal amount = request.getAmount();
        BigDecimal discount = BigDecimal.valueOf(member.getLevel().getDiscount());
        BigDecimal discountedAmount = amount.multiply(discount);

        int pointsToUse = request.getPointsToUse() != null ? request.getPointsToUse() : 0;
        if (pointsToUse > member.getPoints()) {
            throw new RuntimeException("积分不足");
        }

        BigDecimal pointsDeduction = BigDecimal.valueOf(pointsToUse).divide(BigDecimal.valueOf(100));
        BigDecimal finalAmount = discountedAmount.subtract(pointsDeduction);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        int pointsEarned = finalAmount.multiply(BigDecimal.valueOf(member.getLevel().getPointRate()))
                .intValue();

        String transactionNo = generateTransactionNo();
        Transaction transaction = new Transaction(
                transactionNo,
                member,
                TransactionType.CONSUMPTION,
                finalAmount,
                request.getDescription()
        );
        transaction.setPointsEarned(BigDecimal.valueOf(pointsEarned));
        transaction.setPointsUsed(BigDecimal.valueOf(pointsToUse));

        member.setTotalConsumption(member.getTotalConsumption().add(finalAmount));
        member.setPoints(member.getPoints() - pointsToUse + pointsEarned);

        memberService.updateMemberLevel(member);

        return transactionRepository.save(transaction);
    }

    public Transaction recharge(RechargeRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        List<StoredValueCard> cards = storedValueCardRepository.findByMemberIdAndActiveTrue(member.getId());
        StoredValueCard card;
        if (cards.isEmpty()) {
            String cardNo = generateCardNo();
            card = new StoredValueCard(cardNo, member);
            card = storedValueCardRepository.save(card);
        } else {
            card = cards.get(0);
        }

        BigDecimal amount = request.getAmount();
        card.setBalance(card.getBalance().add(amount));
        storedValueCardRepository.save(card);

        String recordNo = generateRecordNo();
        RechargeRecord rechargeRecord = new RechargeRecord(
                recordNo,
                member,
                card,
                amount,
                request.getDescription()
        );
        rechargeRecordRepository.save(rechargeRecord);

        String transactionNo = generateTransactionNo();
        Transaction transaction = new Transaction(
                transactionNo,
                member,
                TransactionType.RECHARGE,
                amount,
                "储值卡充值"
        );

        member.setTotalConsumption(member.getTotalConsumption().add(amount));
        member.setStoredBalance(member.getStoredBalance().add(amount));

        memberService.updateMemberLevel(member);

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getMemberTransactions(Long memberId) {
        return transactionRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public Optional<Transaction> findByTransactionNo(String transactionNo) {
        return transactionRepository.findByTransactionNo(transactionNo);
    }

    private String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TXN" + timestamp + random;
    }

    private String generateCardNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CARD" + timestamp + random;
    }

    private String generateRecordNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REC" + timestamp + random;
    }
}

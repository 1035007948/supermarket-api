package com.supermarket.service.impl;

import com.supermarket.entity.Member;
import com.supermarket.entity.StoredValueCard;
import com.supermarket.entity.StoredValueCardTransaction;
import com.supermarket.enums.MemberLevel;
import com.supermarket.repository.MemberRepository;
import com.supermarket.repository.StoredValueCardRepository;
import com.supermarket.repository.StoredValueCardTransactionRepository;
import com.supermarket.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    @Autowired
    private StoredValueCardTransactionRepository storedValueCardTransactionRepository;

    @Override
    @Transactional
    public Member createMember(Member member) {
        member.setLevel(MemberLevel.REGULAR);
        member.setPoints(0);
        member.setTotalConsumption(BigDecimal.ZERO);
        member.setIsActive(true);
        Member savedMember = memberRepository.save(member);
        createStoredValueCard(savedMember.getId());
        return savedMember;
    }

    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在，ID: " + id));
    }

    @Override
    public Member getMemberByPhone(String phone) {
        return memberRepository.findByPhone(phone)
                .orElseThrow(() -> new EntityNotFoundException("会员不存在，手机号: " + phone));
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Page<Member> getMembersPage(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Member updateMember(Long id, Member member) {
        Member existingMember = getMemberById(id);
        existingMember.setName(member.getName());
        existingMember.setPhone(member.getPhone());
        existingMember.setEmail(member.getEmail());
        existingMember.setBirthday(member.getBirthday());
        return memberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        member.setIsActive(false);
        memberRepository.save(member);
    }

    @Override
    public List<Member> getMembersByLevel(MemberLevel level) {
        return memberRepository.findByLevel(level);
    }

    @Override
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Member updateMemberLevel(Long id) {
        Member member = getMemberById(id);
        int points = member.getPoints();

        if (points >= MemberLevel.GOLD.getMinPoints()) {
            member.setLevel(MemberLevel.GOLD);
        } else if (points >= MemberLevel.SILVER.getMinPoints()) {
            member.setLevel(MemberLevel.SILVER);
        } else {
            member.setLevel(MemberLevel.REGULAR);
        }

        return memberRepository.save(member);
    }

    @Override
    public Map<String, Object> calculateDiscount(Long memberId, BigDecimal originalAmount) {
        Member member = getMemberById(memberId);
        MemberLevel level = member.getLevel();

        BigDecimal discountRate = BigDecimal.valueOf(level.getDiscountRate());
        BigDecimal discountAmount = originalAmount.multiply(BigDecimal.ONE.subtract(discountRate));
        BigDecimal finalAmount = originalAmount.multiply(discountRate);

        int bonusPoints = originalAmount.multiply(BigDecimal.valueOf(level.getBonusPointsRate())).divide(BigDecimal.valueOf(100)).intValue();

        Map<String, Object> result = new HashMap<>();
        result.put("memberLevel", level.getDescription());
        result.put("discountRate", level.getDiscountRate());
        result.put("originalAmount", originalAmount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", finalAmount);
        result.put("bonusPoints", bonusPoints);

        return result;
    }

    @Override
    @Transactional
    public Member addPoints(Long memberId, BigDecimal amount) {
        Member member = getMemberById(memberId);
        MemberLevel level = member.getLevel();

        int pointsToAdd = amount.multiply(BigDecimal.valueOf(level.getBonusPointsRate() + 100))
                .divide(BigDecimal.valueOf(100)).intValue();

        member.setPoints(member.getPoints() + pointsToAdd);
        member.setTotalConsumption(member.getTotalConsumption().add(amount));

        return updateMemberLevel(memberId);
    }

    @Override
    @Transactional
    public Member deductPoints(Long memberId, Integer points) {
        Member member = getMemberById(memberId);
        if (member.getPoints() < points) {
            throw new IllegalStateException("积分不足");
        }
        member.setPoints(member.getPoints() - points);
        return memberRepository.save(member);
    }

    @Override
    public BigDecimal redeemPoints(Long memberId, Integer points) {
        Member member = getMemberById(memberId);
        if (member.getPoints() < points) {
            throw new IllegalStateException("积分不足");
        }
        return BigDecimal.valueOf(points).divide(BigDecimal.valueOf(100));
    }

    @Override
    @Transactional
    public StoredValueCard createStoredValueCard(Long memberId) {
        StoredValueCard card = new StoredValueCard();
        card.setCardNo(UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        card.setMemberId(memberId);
        card.setBalance(BigDecimal.ZERO);
        card.setTotalRecharge(BigDecimal.ZERO);
        card.setTotalConsumption(BigDecimal.ZERO);
        card.setIsActive(true);
        return storedValueCardRepository.save(card);
    }

    @Override
    public StoredValueCard getStoredValueCardById(Long id) {
        return storedValueCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("储值卡不存在，ID: " + id));
    }

    @Override
    public StoredValueCard getStoredValueCardByCardNo(String cardNo) {
        return storedValueCardRepository.findByCardNo(cardNo)
                .orElseThrow(() -> new EntityNotFoundException("储值卡不存在，卡号: " + cardNo));
    }

    @Override
    public List<StoredValueCard> getStoredValueCardsByMemberId(Long memberId) {
        return storedValueCardRepository.findByMemberId(memberId);
    }

    @Override
    @Transactional
    public StoredValueCard rechargeStoredValueCard(Long cardId, BigDecimal amount, String remark) {
        StoredValueCard card = getStoredValueCardById(cardId);
        if (!card.getIsActive()) {
            throw new IllegalStateException("储值卡未激活");
        }

        card.setBalance(card.getBalance().add(amount));
        card.setTotalRecharge(card.getTotalRecharge().add(amount));
        StoredValueCard savedCard = storedValueCardRepository.save(card);

        StoredValueCardTransaction transaction = new StoredValueCardTransaction();
        transaction.setCardId(cardId);
        transaction.setMemberId(card.getMemberId());
        transaction.setAmount(amount);
        transaction.setType("RECHARGE");
        transaction.setRemark(remark);
        transaction.setCreateTime(LocalDateTime.now());
        storedValueCardTransactionRepository.save(transaction);

        return savedCard;
    }

    @Override
    @Transactional
    public StoredValueCard consumeStoredValueCard(Long cardId, BigDecimal amount, Long orderId, String remark) {
        StoredValueCard card = getStoredValueCardById(cardId);
        if (!card.getIsActive()) {
            throw new IllegalStateException("储值卡未激活");
        }
        if (card.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("储值卡余额不足");
        }

        card.setBalance(card.getBalance().subtract(amount));
        card.setTotalConsumption(card.getTotalConsumption().add(amount));
        StoredValueCard savedCard = storedValueCardRepository.save(card);

        StoredValueCardTransaction transaction = new StoredValueCardTransaction();
        transaction.setCardId(cardId);
        transaction.setMemberId(card.getMemberId());
        transaction.setAmount(amount.negate());
        transaction.setType("CONSUMPTION");
        transaction.setRemark(remark);
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setOrderId(orderId);
        storedValueCardTransactionRepository.save(transaction);

        return savedCard;
    }

    @Override
    public List<StoredValueCardTransaction> getStoredValueCardTransactionsByCardId(Long cardId) {
        return storedValueCardTransactionRepository.findByCardId(cardId);
    }

    @Override
    public List<StoredValueCardTransaction> getStoredValueCardTransactionsByMemberId(Long memberId) {
        return storedValueCardTransactionRepository.findByMemberId(memberId);
    }

    @Override
    @Transactional
    public StoredValueCard activateStoredValueCard(Long cardId) {
        StoredValueCard card = getStoredValueCardById(cardId);
        card.setIsActive(true);
        return storedValueCardRepository.save(card);
    }

    @Override
    @Transactional
    public StoredValueCard deactivateStoredValueCard(Long cardId) {
        StoredValueCard card = getStoredValueCardById(cardId);
        card.setIsActive(false);
        return storedValueCardRepository.save(card);
    }
}
package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.MemberRequest;
import com.supermarket.dto.MemberResponse;
import com.supermarket.dto.RechargeRequest;
import com.supermarket.dto.TransactionResponse;
import com.supermarket.entity.*;
import com.supermarket.repository.MemberRepository;
import com.supermarket.repository.MemberTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final MemberTransactionRepository transactionRepository;
    
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(400, "手机号已注册");
        }
        
        if (request.getEmail() != null && memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已注册");
        }
        
        Member member = new Member();
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        member.setLevel(MemberLevel.NORMAL);
        member.setPoints(0);
        member.setTotalSpent(BigDecimal.ZERO);
        member.setBalance(BigDecimal.ZERO);
        
        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }
    
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        return toResponse(member);
    }
    
    public MemberResponse getMemberByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        return toResponse(member);
    }
    
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::toResponse);
    }
    
    public Page<MemberResponse> getMembersByLevel(MemberLevel level, Pageable pageable) {
        return memberRepository.findByLevel(level, pageable).map(this::toResponse);
    }
    
    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        
        if (!member.getPhone().equals(request.getPhone()) && 
            memberRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(400, "手机号已被使用");
        }
        
        if (request.getEmail() != null && 
            !request.getEmail().equals(member.getEmail()) && 
            memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已被使用");
        }
        
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setEmail(request.getEmail());
        
        Member updated = memberRepository.save(member);
        return toResponse(updated);
    }
    
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new BusinessException(404, "会员不存在");
        }
        memberRepository.deleteById(id);
    }
    
    @Transactional
    public MemberResponse recharge(Long id, RechargeRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        
        BigDecimal bonus = calculateRechargeBonus(request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(bonus);
        
        member.setBalance(member.getBalance().add(totalAmount));
        
        Integer earnedPoints = request.getAmount().multiply(BigDecimal.valueOf(0.1)).intValue();
        member.setPoints(member.getPoints() + earnedPoints);
        
        member.setTotalSpent(member.getTotalSpent().add(request.getAmount()));
        
        updateMemberLevel(member);
        
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(member.getId());
        transaction.setType(TransactionType.RECHARGE);
        transaction.setAmount(totalAmount);
        transaction.setPoints(earnedPoints);
        transaction.setDescription("储值卡充值，赠送金额: " + bonus + "，获得积分: " + earnedPoints);
        
        transactionRepository.save(transaction);
        Member updated = memberRepository.save(member);
        
        return toResponse(updated);
    }
    
    @Transactional
    public MemberResponse addPoints(Long id, Integer points) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        
        member.setPoints(member.getPoints() + points);
        
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(member.getId());
        transaction.setType(TransactionType.POINTS_EARN);
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setPoints(points);
        transaction.setDescription("消费获得积分");
        
        transactionRepository.save(transaction);
        Member updated = memberRepository.save(member);
        
        return toResponse(updated);
    }
    
    @Transactional
    public MemberResponse usePoints(Long id, Integer points) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        
        if (member.getPoints() < points) {
            throw new BusinessException(400, "积分不足");
        }
        
        member.setPoints(member.getPoints() - points);
        
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(member.getId());
        transaction.setType(TransactionType.POINTS_USE);
        transaction.setAmount(BigDecimal.ZERO);
        transaction.setPoints(-points);
        transaction.setDescription("使用积分抵扣");
        
        transactionRepository.save(transaction);
        Member updated = memberRepository.save(member);
        
        return toResponse(updated);
    }
    
    @Transactional
    public MemberResponse consume(Long id, BigDecimal amount, Long orderId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "会员不存在"));
        
        BigDecimal discount = calculateDiscount(member.getLevel(), amount);
        BigDecimal actualAmount = amount.subtract(discount);
        
        if (member.getBalance().compareTo(actualAmount) < 0) {
            throw new BusinessException(400, "余额不足");
        }
        
        member.setBalance(member.getBalance().subtract(actualAmount));
        member.setTotalSpent(member.getTotalSpent().add(actualAmount));
        
        updateMemberLevel(member);
        
        MemberTransaction transaction = new MemberTransaction();
        transaction.setMemberId(member.getId());
        transaction.setType(TransactionType.CONSUME);
        transaction.setAmount(actualAmount);
        transaction.setRelatedOrderId(orderId);
        transaction.setDescription("消费，优惠: " + discount);
        
        transactionRepository.save(transaction);
        Member updated = memberRepository.save(member);
        
        return toResponse(updated);
    }
    
    public Page<TransactionResponse> getMemberTransactions(Long memberId, Pageable pageable) {
        return transactionRepository.findByMemberId(memberId, pageable)
                .map(this::toTransactionResponse);
    }
    
    private BigDecimal calculateRechargeBonus(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(500)) >= 0) {
            return amount.multiply(BigDecimal.valueOf(0.1));
        } else if (amount.compareTo(BigDecimal.valueOf(200)) >= 0) {
            return amount.multiply(BigDecimal.valueOf(0.05));
        }
        return BigDecimal.ZERO;
    }
    
    private BigDecimal calculateDiscount(MemberLevel level, BigDecimal amount) {
        return switch (level) {
            case GOLD -> amount.multiply(BigDecimal.valueOf(0.1));
            case SILVER -> amount.multiply(BigDecimal.valueOf(0.05));
            default -> BigDecimal.ZERO;
        };
    }
    
    private void updateMemberLevel(Member member) {
        BigDecimal totalSpent = member.getTotalSpent();
        if (totalSpent.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            member.setLevel(MemberLevel.GOLD);
        } else if (totalSpent.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            member.setLevel(MemberLevel.SILVER);
        }
    }
    
    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getPhone(),
                member.getEmail(),
                member.getLevel(),
                member.getPoints(),
                member.getTotalSpent(),
                member.getBalance(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
    
    private TransactionResponse toTransactionResponse(MemberTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getMemberId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getPoints(),
                transaction.getDescription(),
                transaction.getRelatedOrderId(),
                transaction.getCreatedAt()
        );
    }
}

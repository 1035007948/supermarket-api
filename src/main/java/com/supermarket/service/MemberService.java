package com.supermarket.service;

import com.supermarket.entity.Member;
import com.supermarket.entity.StoredValueCard;
import com.supermarket.entity.StoredValueCardTransaction;
import com.supermarket.enums.MemberLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MemberService {

    Member createMember(Member member);

    Member getMemberById(Long id);

    Member getMemberByPhone(String phone);

    List<Member> getAllMembers();

    Page<Member> getMembersPage(Pageable pageable);

    Member updateMember(Long id, Member member);

    void deleteMember(Long id);

    List<Member> getMembersByLevel(MemberLevel level);

    List<Member> searchMembersByName(String name);

    Member updateMemberLevel(Long id);

    Map<String, Object> calculateDiscount(Long memberId, BigDecimal originalAmount);

    Member addPoints(Long memberId, BigDecimal amount);

    Member deductPoints(Long memberId, Integer points);

    BigDecimal redeemPoints(Long memberId, Integer points);

    StoredValueCard createStoredValueCard(Long memberId);

    StoredValueCard getStoredValueCardById(Long id);

    StoredValueCard getStoredValueCardByCardNo(String cardNo);

    List<StoredValueCard> getStoredValueCardsByMemberId(Long memberId);

    StoredValueCard rechargeStoredValueCard(Long cardId, BigDecimal amount, String remark);

    StoredValueCard consumeStoredValueCard(Long cardId, BigDecimal amount, Long orderId, String remark);

    List<StoredValueCardTransaction> getStoredValueCardTransactionsByCardId(Long cardId);

    List<StoredValueCardTransaction> getStoredValueCardTransactionsByMemberId(Long memberId);

    StoredValueCard activateStoredValueCard(Long cardId);

    StoredValueCard deactivateStoredValueCard(Long cardId);
}
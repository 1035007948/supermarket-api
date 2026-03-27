package com.supermarket.member.service;

import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.StoredValueCard;
import com.supermarket.member.repository.MemberRepository;
import com.supermarket.member.repository.StoredValueCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StoredValueCardService {

    @Autowired
    private StoredValueCardRepository storedValueCardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public StoredValueCard createCard(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("会员不存在"));

        String cardNo = generateCardNo();
        StoredValueCard card = new StoredValueCard(cardNo, member);

        return storedValueCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public Optional<StoredValueCard> findById(Long id) {
        return storedValueCardRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<StoredValueCard> findByCardNo(String cardNo) {
        return storedValueCardRepository.findByCardNo(cardNo);
    }

    @Transactional(readOnly = true)
    public List<StoredValueCard> findByMemberId(Long memberId) {
        return storedValueCardRepository.findByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<StoredValueCard> findActiveCardsByMemberId(Long memberId) {
        return storedValueCardRepository.findByMemberIdAndActiveTrue(memberId);
    }

    public StoredValueCard deactivateCard(Long cardId) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        card.setActive(false);
        return storedValueCardRepository.save(card);
    }

    public StoredValueCard activateCard(Long cardId) {
        StoredValueCard card = storedValueCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("储值卡不存在"));

        card.setActive(true);
        return storedValueCardRepository.save(card);
    }

    private String generateCardNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CARD" + timestamp + random;
    }
}

package com.supermarket.repository;

import com.supermarket.entity.StoredValueCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoredValueCardRepository extends JpaRepository<StoredValueCard, Long> {

    Optional<StoredValueCard> findByCardNo(String cardNo);

    List<StoredValueCard> findByMemberId(Long memberId);

    List<StoredValueCard> findByMemberIdAndIsActive(Long memberId, Boolean isActive);
}
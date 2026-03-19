package com.supermarket.repository;

import com.supermarket.entity.StoredValueCardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoredValueCardTransactionRepository extends JpaRepository<StoredValueCardTransaction, Long> {

    List<StoredValueCardTransaction> findByCardId(Long cardId);

    List<StoredValueCardTransaction> findByMemberId(Long memberId);

    List<StoredValueCardTransaction> findByOrderId(Long orderId);
}
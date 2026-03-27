package com.supermarket.member.repository;

import com.supermarket.member.entity.Transaction;
import com.supermarket.member.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<Transaction> findByMemberIdAndTypeOrderByCreatedAtDesc(Long memberId, TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.member.id = :memberId AND t.createdAt BETWEEN :startTime AND :endTime")
    List<Transaction> findByMemberIdAndDateRange(@Param("memberId") Long memberId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    Optional<Transaction> findByTransactionNo(String transactionNo);
}

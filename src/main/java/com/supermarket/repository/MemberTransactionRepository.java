package com.supermarket.repository;

import com.supermarket.entity.MemberTransaction;
import com.supermarket.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberTransactionRepository extends JpaRepository<MemberTransaction, Long> {
    Page<MemberTransaction> findByMemberId(Long memberId, Pageable pageable);
    List<MemberTransaction> findByMemberIdAndType(Long memberId, TransactionType type);
}

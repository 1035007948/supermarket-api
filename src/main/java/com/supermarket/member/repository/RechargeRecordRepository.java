package com.supermarket.member.repository;

import com.supermarket.member.entity.RechargeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {

    List<RechargeRecord> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<RechargeRecord> findByCardIdOrderByCreatedAtDesc(Long cardId);

    Optional<RechargeRecord> findByRecordNo(String recordNo);
}

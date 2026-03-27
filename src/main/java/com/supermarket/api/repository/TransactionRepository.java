package com.supermarket.api.repository;

import com.supermarket.api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录数据访问接口
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    /**
     * 根据会员ID查询交易记录（分页）
     */
    Page<Transaction> findByMemberIdOrderByCreateTimeDesc(Long memberId, Pageable pageable);

    /**
     * 根据会员ID和交易类型查询交易记录
     */
    List<Transaction> findByMemberIdAndTypeOrderByCreateTimeDesc(Long memberId, String type);

    /**
     * 查询会员在指定时间范围内的交易记录
     */
    List<Transaction> findByMemberIdAndCreateTimeBetween(Long memberId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据交易单号查询交易记录
     */
    Transaction findByTransactionNo(String transactionNo);
}

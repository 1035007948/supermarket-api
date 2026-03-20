package com.supermarket.repository;

import com.supermarket.entity.Order;
import com.supermarket.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNo(String orderNo);
    Page<Order> findByMemberId(Long memberId, Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByStatusInAndPreparedAtBefore(List<OrderStatus> statuses, LocalDateTime time);
    List<Order> findByStatusInAndDeliveredAtBefore(List<OrderStatus> statuses, LocalDateTime time);
    boolean existsByOrderNo(String orderNo);
}

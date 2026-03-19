package com.supermarket.repository;

import com.supermarket.entity.Order;
import com.supermarket.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByMemberId(Long memberId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByMemberIdAndStatus(Long memberId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses AND o.shipTime <= :reminderTime AND o.reminderSent = false")
    List<Order> findOrdersForReminder(List<OrderStatus> statuses, LocalDateTime reminderTime);
}
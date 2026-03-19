package com.supermarket.service;

import com.supermarket.entity.Order;
import com.supermarket.entity.OrderItem;
import com.supermarket.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Order createOrder(Order order, List<OrderItem> orderItems);

    Order getOrderById(Long id);

    Order getOrderByOrderNo(String orderNo);

    List<Order> getOrdersByMemberId(Long memberId);

    List<Order> getOrdersByStatus(OrderStatus status);

    Page<Order> getOrdersPage(Pageable pageable);

    Order updateOrderStatus(Long id, OrderStatus status);

    Order payOrder(Long id, String paymentMethod);

    Order prepareOrder(Long id);

    Order shipOrder(Long id);

    Order completeOrder(Long id);

    Order cancelOrder(Long id, String reason);

    Order applyRefund(Long id, String reason);

    Order auditRefund(Long id, Boolean approved, String auditRemark);

    void processReminders();

    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}
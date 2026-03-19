package com.supermarket.service.impl;

import com.supermarket.entity.Order;
import com.supermarket.entity.OrderItem;
import com.supermarket.enums.OrderStatus;
import com.supermarket.enums.PaymentMethod;
import com.supermarket.repository.OrderRepository;
import com.supermarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(Order order, List<OrderItem> orderItems) {
        String orderNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        order.setOrderNo(orderNo);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setIsDeleted(false);
        order.setReminderSent(false);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在，ID: " + id));
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在，订单号: " + orderNo));
    }

    @Override
    public List<Order> getOrdersByMemberId(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Page<Order> getOrdersPage(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payOrder(Long id, String paymentMethod) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("订单状态不正确，无法支付");
        }
        order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        order.setStatus(OrderStatus.PENDING_PREPARATION);
        order.setPayTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order prepareOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING_PREPARATION) {
            throw new IllegalStateException("订单状态不正确，无法备货");
        }
        order.setStatus(OrderStatus.PENDING_SHIPMENT);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order shipOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT && order.getStatus() != OrderStatus.PENDING_PICKUP) {
            throw new IllegalStateException("订单状态不正确，无法发货");
        }
        order.setStatus(OrderStatus.PENDING_PICKUP);
        order.setShipTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order completeOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.PENDING_PICKUP && order.getStatus() != OrderStatus.PENDING_SHIPMENT) {
            throw new IllegalStateException("订单状态不正确，无法完成");
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long id, String reason) {
        Order order = getOrderById(id);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("订单已完成或已取消，无法取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setRemark(reason);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order applyRefund(Long id, String reason) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("只有已完成的订单才能申请退款");
        }
        order.setRemark("退款申请中: " + reason);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order auditRefund(Long id, Boolean approved, String auditRemark) {
        Order order = getOrderById(id);
        if (approved) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setRemark("退款已批准: " + auditRemark);
        } else {
            order.setRemark("退款已拒绝: " + auditRemark);
        }
        return orderRepository.save(order);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void processReminders() {
        LocalDateTime reminderTime = LocalDateTime.now().minusMinutes(15);
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.PENDING_PICKUP, OrderStatus.PENDING_SHIPMENT);
        List<Order> orders = orderRepository.findOrdersForReminder(statuses, reminderTime);

        for (Order order : orders) {
            System.out.println("发送提醒通知: 订单号 " + order.getOrderNo() + " 已发货/备货完成15分钟，请及时取货");
            order.setReminderSent(true);
            orderRepository.save(order);
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getOrderItems();
    }
}
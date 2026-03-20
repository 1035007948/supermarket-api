package com.supermarket.scheduler;

import com.supermarket.entity.Order;
import com.supermarket.entity.OrderStatus;
import com.supermarket.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationScheduler {
    
    private final OrderRepository orderRepository;
    
    @Scheduled(fixedRate = 60000)
    public void checkOrderNotifications() {
        log.info("开始检查订单通知...");
        
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        
        List<Order> preparedOrders = orderRepository.findByStatusInAndPreparedAtBefore(
                Arrays.asList(OrderStatus.READY_FOR_PICKUP, OrderStatus.READY_FOR_DELIVERY),
                fifteenMinutesAgo
        );
        
        for (Order order : preparedOrders) {
            sendNotification(order);
        }
        
        log.info("订单通知检查完成，共处理 {} 条订单", preparedOrders.size());
    }
    
    private void sendNotification(Order order) {
        String message = String.format(
                "订单 %s 已备货/发货完成超过15分钟，请及时处理。订单状态: %s",
                order.getOrderNo(),
                order.getStatus()
        );
        
        log.info("发送通知: {}", message);
        
        logNotification(order.getId(), order.getOrderNo(), message);
    }
    
    private void logNotification(Long orderId, String orderNo, String message) {
        log.info("订单ID: {}, 订单号: {}, 通知内容: {}", orderId, orderNo, message);
    }
}

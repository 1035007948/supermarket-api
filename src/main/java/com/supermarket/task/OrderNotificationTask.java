package com.supermarket.task;

import com.supermarket.entity.Order;
import com.supermarket.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderNotificationTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(fixedRate = 60000)
    public void checkAndNotifyOrders() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        List<Order> orders = orderMapper.selectOrdersNeedNotify(fifteenMinutesAgo);

        for (Order order : orders) {
            try {
                notifyCustomer(order);

                order.setNotifyStatus(1);
                order.setNotifyTime(LocalDateTime.now());
                orderMapper.updateById(order);

                log.info("订单提醒通知已发送: {}", order.getOrderNo());
            } catch (Exception e) {
                log.error("订单提醒通知发送失败: {}", order.getOrderNo(), e);
            }
        }
    }

    private void notifyCustomer(Order order) {
        String message = buildNotificationMessage(order);

        if (order.getMemberPhone() != null) {
            log.info("发送短信提醒到 {}: {}", order.getMemberPhone(), message);
        }

        log.info("订单 {} 提醒通知: {}", order.getOrderNo(), message);
    }

    private String buildNotificationMessage(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("【超市通知】您的订单").append(order.getOrderNo());

        if ("PENDING_PICKUP".equals(order.getStatus())) {
            sb.append("已备货完成，请尽快到店取货。");
        } else if ("SHIPPED".equals(order.getStatus())) {
            sb.append("已发货，请注意查收。");
        }

        return sb.toString();
    }
}

package com.supermarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.OrderDTO;
import com.supermarket.dto.RefundDTO;
import com.supermarket.dto.RefundReviewDTO;
import com.supermarket.entity.Order;
import com.supermarket.vo.OrderVO;
import com.supermarket.vo.RefundVO;

public interface OrderService extends IService<Order> {

    OrderVO createOrder(OrderDTO dto);

    void payOrder(Long orderId);

    void prepareOrder(Long orderId);

    void shipOrder(Long orderId);

    void completeOrder(Long orderId);

    void cancelOrder(Long orderId, String reason);

    Page<OrderVO> getOrderPage(Integer page, Integer size, String orderNo, String status, Long memberId);

    OrderVO getOrderById(Long id);

    RefundVO applyRefund(RefundDTO dto);

    void reviewRefund(RefundReviewDTO dto);
}

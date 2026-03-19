package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.Order;
import com.supermarket.entity.OrderItem;
import com.supermarket.enums.OrderStatus;
import com.supermarket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> createOrder(@RequestBody Map<String, Object> request) {
        Order order = new Order();
        order.setMemberId(Long.valueOf(request.get("memberId").toString()));
        order.setTotalAmount(new java.math.BigDecimal(request.get("totalAmount").toString()));
        order.setPayAmount(new java.math.BigDecimal(request.get("payAmount").toString()));
        if (request.get("discountAmount") != null) {
            order.setDiscountAmount(new java.math.BigDecimal(request.get("discountAmount").toString()));
        }
        order.setRemark((String) request.get("remark"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) request.get("orderItems");
        List<OrderItem> orderItems = itemsMap.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(Long.valueOf(item.get("productId").toString()));
            orderItem.setProductName((String) item.get("productName"));
            orderItem.setProductPrice(new java.math.BigDecimal(item.get("productPrice").toString()));
            orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
            orderItem.setTotalPrice(new java.math.BigDecimal(item.get("totalPrice").toString()));
            return orderItem;
        }).toList();

        Order createdOrder = orderService.createOrder(order, orderItems);
        return Result.success(createdOrder);
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return Result.success(order);
    }

    @GetMapping("/no/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        return Result.success(order);
    }

    @GetMapping("/member/{memberId}")
    public Result<List<Order>> getOrdersByMemberId(@PathVariable Long memberId) {
        List<Order> orders = orderService.getOrdersByMemberId(memberId);
        return Result.success(orders);
    }

    @GetMapping("/status/{status}")
    public Result<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return Result.success(orders);
    }

    @GetMapping("/page")
    public Result<Page<Order>> getOrdersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = orderService.getOrdersPage(pageable);
        return Result.success(orderPage);
    }

    @PutMapping("/{id}/status")
    public Result<Order> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return Result.success(updatedOrder);
    }

    @PutMapping("/{id}/pay")
    public Result<Order> payOrder(@PathVariable Long id, @RequestParam String paymentMethod) {
        Order order = orderService.payOrder(id, paymentMethod);
        return Result.success(order);
    }

    @PutMapping("/{id}/prepare")
    public Result<Order> prepareOrder(@PathVariable Long id) {
        Order order = orderService.prepareOrder(id);
        return Result.success(order);
    }

    @PutMapping("/{id}/ship")
    public Result<Order> shipOrder(@PathVariable Long id) {
        Order order = orderService.shipOrder(id);
        return Result.success(order);
    }

    @PutMapping("/{id}/complete")
    public Result<Order> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return Result.success(order);
    }

    @PutMapping("/{id}/cancel")
    public Result<Order> cancelOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, String> request) {
        String reason = request != null && request.get("reason") != null ? request.get("reason") : "";
        Order order = orderService.cancelOrder(id, reason);
        return Result.success(order);
    }

    @PostMapping("/{id}/refund/apply")
    public Result<Order> applyRefund(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Order order = orderService.applyRefund(id, reason);
        return Result.success(order);
    }

    @PostMapping("/{id}/refund/audit")
    public Result<Order> auditRefund(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Boolean approved = Boolean.valueOf(request.get("approved").toString());
        String auditRemark = (String) request.get("auditRemark");
        Order order = orderService.auditRefund(id, approved, auditRemark);
        return Result.success(order);
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long id) {
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(id);
        return Result.success(orderItems);
    }

    @PostMapping("/reminders/process")
    public Result<Void> processReminders() {
        orderService.processReminders();
        return Result.success();
    }
}
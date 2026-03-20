package com.supermarket.controller;

import com.supermarket.common.ApiResponse;
import com.supermarket.dto.OrderRequest;
import com.supermarket.dto.OrderResponse;
import com.supermarket.entity.OrderStatus;
import com.supermarket.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ApiResponse.success("订单创建成功", response);
    }
    
    @GetMapping
    public ApiResponse<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/order-no/{orderNo}")
    public ApiResponse<OrderResponse> getOrderByOrderNo(@PathVariable String orderNo) {
        OrderResponse response = orderService.getOrderByOrderNo(orderNo);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/member/{memberId}")
    public ApiResponse<Page<OrderResponse>> getOrdersByMemberId(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getOrdersByMemberId(memberId, pageable);
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/status/{status}")
    public ApiResponse<Page<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getOrdersByStatus(status, pageable);
        return ApiResponse.success(orders);
    }
    
    @PostMapping("/{id}/pay")
    public ApiResponse<OrderResponse> payOrder(@PathVariable Long id) {
        OrderResponse response = orderService.payOrder(id);
        return ApiResponse.success("支付成功", response);
    }
    
    @PostMapping("/{id}/prepare")
    public ApiResponse<OrderResponse> prepareOrder(@PathVariable Long id) {
        OrderResponse response = orderService.prepareOrder(id);
        return ApiResponse.success("备货完成", response);
    }
    
    @PostMapping("/{id}/deliver")
    public ApiResponse<OrderResponse> deliverOrder(@PathVariable Long id) {
        OrderResponse response = orderService.deliverOrder(id);
        return ApiResponse.success("发货成功", response);
    }
    
    @PostMapping("/{id}/complete")
    public ApiResponse<OrderResponse> completeOrder(@PathVariable Long id) {
        OrderResponse response = orderService.completeOrder(id);
        return ApiResponse.success("订单完成", response);
    }
    
    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        OrderResponse response = orderService.cancelOrder(id, reason);
        return ApiResponse.success("订单已取消", response);
    }
    
    @PostMapping("/{id}/refund")
    public ApiResponse<OrderResponse> requestRefund(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        OrderResponse response = orderService.requestRefund(id, reason);
        return ApiResponse.success("退款申请已提交", response);
    }
    
    @PostMapping("/{id}/approve-refund")
    public ApiResponse<OrderResponse> approveRefund(@PathVariable Long id) {
        OrderResponse response = orderService.approveRefund(id);
        return ApiResponse.success("退款已批准", response);
    }
}

package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.OrderRequest;
import com.supermarket.dto.OrderResponse;
import com.supermarket.entity.*;
import com.supermarket.repository.OrderItemRepository;
import com.supermarket.repository.OrderRepository;
import com.supermarket.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        memberService.getMemberById(request.getMemberId());
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setMemberId(request.getMemberId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setPointsUsed(request.getPointsUsed() != null ? request.getPointsUsed() : 0);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setDeliveryType(request.getDeliveryType());
        order.setRemark(request.getRemark());
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new BusinessException(404, "商品不存在: " + itemRequest.getProductId()));
            
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException(400, "商品库存不足: " + product.getName());
            }
            
            if (product.getStatus() != ProductStatus.ON_SALE && product.getStatus() != ProductStatus.PRE_SALE) {
                throw new BusinessException(400, "商品不可购买: " + product.getName());
            }
            
            product.setStock(product.getStock() - itemRequest.getQuantity());
            if (product.getStock() == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            productRepository.save(product);
            
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }
        
        order.setTotalAmount(totalAmount);
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getPointsUsed() != null && request.getPointsUsed() > 0) {
            discountAmount = BigDecimal.valueOf(request.getPointsUsed() * 0.01);
        }
        order.setDiscountAmount(discountAmount);
        order.setPaidAmount(totalAmount.subtract(discountAmount));
        
        Order savedOrder = orderRepository.save(order);
        
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).orElseThrow();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem.setSpecification(product.getSpecification());
            
            orderItemRepository.save(orderItem);
        }
        
        return getOrderById(savedOrder.getId());
    }
    
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        return toResponse(order);
    }
    
    public OrderResponse getOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        return toResponse(order);
    }
    
    public Page<OrderResponse> getOrdersByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable).map(this::toResponse);
    }
    
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(this::toResponse);
    }
    
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toResponse);
    }
    
    @Transactional
    public OrderResponse payOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(400, "订单状态不正确，无法支付");
        }
        
        order.setStatus(OrderStatus.PREPARING);
        order.setPaidAt(LocalDateTime.now());
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse prepareOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new BusinessException(400, "订单状态不正确，无法备货");
        }
        
        OrderStatus nextStatus = "DELIVERY".equals(order.getDeliveryType()) 
                ? OrderStatus.READY_FOR_DELIVERY 
                : OrderStatus.READY_FOR_PICKUP;
        
        order.setStatus(nextStatus);
        order.setPreparedAt(LocalDateTime.now());
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse deliverOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.READY_FOR_DELIVERY) {
            throw new BusinessException(400, "订单状态不正确，无法发货");
        }
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setDeliveredAt(LocalDateTime.now());
        order.setCompletedAt(LocalDateTime.now());
        
        memberService.addPoints(order.getMemberId(), calculateEarnedPoints(order.getPaidAmount()));
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse completeOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BusinessException(400, "订单状态不正确，无法完成");
        }
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        
        memberService.addPoints(order.getMemberId(), calculateEarnedPoints(order.getPaidAmount()));
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse cancelOrder(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(400, "订单已完成或已取消，无法取消");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(reason);
        
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if (product.getStatus() == ProductStatus.OUT_OF_STOCK && product.getStock() > 0) {
                    product.setStatus(ProductStatus.ON_SALE);
                }
                productRepository.save(product);
            }
        }
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse requestRefund(Long id, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException(400, "只有已完成的订单才能申请退款");
        }
        
        order.setStatus(OrderStatus.REFUNDING);
        order.setCancelReason(reason);
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    @Transactional
    public OrderResponse approveRefund(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在"));
        
        if (order.getStatus() != OrderStatus.REFUNDING) {
            throw new BusinessException(400, "订单状态不正确，无法退款");
        }
        
        order.setStatus(OrderStatus.REFUNDED);
        order.setCancelledAt(LocalDateTime.now());
        
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                if (product.getStatus() == ProductStatus.OUT_OF_STOCK && product.getStock() > 0) {
                    product.setStatus(ProductStatus.ON_SALE);
                }
                productRepository.save(product);
            }
        }
        
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }
    
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD" + timestamp + random;
    }
    
    private Integer calculateEarnedPoints(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.1)).intValue();
    }
    
    private OrderResponse toResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderResponse.OrderItemResponse> itemResponses = items.stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal(),
                        item.getSpecification()
                ))
                .collect(Collectors.toList());
        
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getMemberId(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPaidAmount(),
                order.getPointsUsed(),
                order.getPointsEarned(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverAddress(),
                order.getDeliveryType(),
                order.getPaidAt(),
                order.getPreparedAt(),
                order.getDeliveredAt(),
                order.getCompletedAt(),
                order.getCancelledAt(),
                order.getCancelReason(),
                order.getRemark(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }
}

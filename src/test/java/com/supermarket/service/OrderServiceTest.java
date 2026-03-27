package com.supermarket.service;

import com.supermarket.common.BusinessException;
import com.supermarket.dto.*;
import com.supermarket.entity.*;
import com.supermarket.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderRepository orderRepository;

    private ProductResponse product;
    private MemberResponse member;

    @BeforeEach
    void setUp() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("测试商品");
        productRequest.setPrice(new BigDecimal("100.00"));
        productRequest.setStock(100);
        productRequest.setSpecification("500g");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setStatus(ProductStatus.ON_SALE);
        product = productService.createProduct(productRequest);

        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setName("测试会员");
        memberRequest.setPhone("13900139001");
        member = memberService.createMember(memberRequest);
    }

    @Test
    @DisplayName("创建订单-成功")
    void createOrder_Success() {
        OrderRequest request = createOrderRequest();

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getOrderNo());
        assertEquals(member.getId(), response.getMemberId());
        assertEquals(PaymentMethod.ONLINE_PAYMENT, response.getPaymentMethod());
        assertEquals(OrderStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
    }

    @Test
    @DisplayName("创建订单-库存不足")
    void createOrder_InsufficientStock() {
        OrderRequest request = createOrderRequest();
        request.getItems().get(0).setQuantity(200);

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(request);
        });
    }

    @Test
    @DisplayName("创建订单-商品不可购买")
    void createOrder_ProductNotAvailable() {
        productService.updateProduct(product.getId(), createOffShelfProductRequest());

        OrderRequest request = createOrderRequest();

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(request);
        });
    }

    @Test
    @DisplayName("支付订单-成功")
    void payOrder_Success() {
        OrderResponse order = orderService.createOrder(createOrderRequest());

        OrderResponse response = orderService.payOrder(order.getId());

        assertEquals(OrderStatus.PREPARING, response.getStatus());
        assertNotNull(response.getPaidAt());
    }

    @Test
    @DisplayName("支付订单-状态不正确")
    void payOrder_InvalidStatus() {
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.payOrder(order.getId());

        assertThrows(BusinessException.class, () -> {
            orderService.payOrder(order.getId());
        });
    }

    @Test
    @DisplayName("备货完成-自提订单")
    void prepareOrder_Pickup() {
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.payOrder(order.getId());

        OrderResponse response = orderService.prepareOrder(order.getId());

        assertEquals(OrderStatus.READY_FOR_PICKUP, response.getStatus());
        assertNotNull(response.getPreparedAt());
    }

    @Test
    @DisplayName("备货完成-配送订单")
    void prepareOrder_Delivery() {
        OrderRequest request = createOrderRequest();
        request.setDeliveryType("DELIVERY");
        OrderResponse order = orderService.createOrder(request);
        orderService.payOrder(order.getId());

        OrderResponse response = orderService.prepareOrder(order.getId());

        assertEquals(OrderStatus.READY_FOR_DELIVERY, response.getStatus());
    }

    @Test
    @DisplayName("发货-成功")
    void deliverOrder_Success() {
        OrderRequest request = createOrderRequest();
        request.setDeliveryType("DELIVERY");
        OrderResponse order = orderService.createOrder(request);
        orderService.payOrder(order.getId());
        orderService.prepareOrder(order.getId());

        OrderResponse response = orderService.deliverOrder(order.getId());

        assertEquals(OrderStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getDeliveredAt());
        assertNotNull(response.getCompletedAt());
    }

    @Test
    @DisplayName("完成订单-自提")
    void completeOrder_Success() {
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.payOrder(order.getId());
        orderService.prepareOrder(order.getId());

        OrderResponse response = orderService.completeOrder(order.getId());

        assertEquals(OrderStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getCompletedAt());
    }

    @Test
    @DisplayName("取消订单-成功")
    void cancelOrder_Success() {
        OrderResponse order = orderService.createOrder(createOrderRequest());

        OrderResponse response = orderService.cancelOrder(order.getId(), "不想要了");

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        assertNotNull(response.getCancelledAt());
        assertEquals("不想要了", response.getCancelReason());
    }

    @Test
    @DisplayName("取消订单-恢复库存")
    void cancelOrder_RestoreStock() {
        int initialStock = productService.getProductById(product.getId()).getStock();
        
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.cancelOrder(order.getId(), "不想要了");

        ProductResponse updatedProduct = productService.getProductById(product.getId());
        assertEquals(initialStock, updatedProduct.getStock());
    }

    @Test
    @DisplayName("申请退款-成功")
    void requestRefund_Success() {
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.payOrder(order.getId());
        orderService.prepareOrder(order.getId());
        orderService.completeOrder(order.getId());

        OrderResponse response = orderService.requestRefund(order.getId(), "质量问题");

        assertEquals(OrderStatus.REFUNDING, response.getStatus());
        assertEquals("质量问题", response.getCancelReason());
    }

    @Test
    @DisplayName("批准退款-成功")
    void approveRefund_Success() {
        OrderResponse order = orderService.createOrder(createOrderRequest());
        orderService.payOrder(order.getId());
        orderService.prepareOrder(order.getId());
        orderService.completeOrder(order.getId());
        orderService.requestRefund(order.getId(), "质量问题");

        OrderResponse response = orderService.approveRefund(order.getId());

        assertEquals(OrderStatus.REFUNDED, response.getStatus());
        assertNotNull(response.getCancelledAt());
    }

    @Test
    @DisplayName("按会员查询订单")
    void getOrdersByMemberId() {
        orderService.createOrder(createOrderRequest());

        Page<OrderResponse> page = orderService.getOrdersByMemberId(
                member.getId(), 
                PageRequest.of(0, 10)
        );

        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    @DisplayName("按状态查询订单")
    void getOrdersByStatus() {
        OrderResponse order = orderService.createOrder(createOrderRequest());

        Page<OrderResponse> page = orderService.getOrdersByStatus(
                OrderStatus.PENDING_PAYMENT, 
                PageRequest.of(0, 10)
        );

        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    @DisplayName("使用积分抵扣")
    void createOrder_WithPointsDiscount() {
        memberService.addPoints(member.getId(), 1000);

        OrderRequest request = createOrderRequest();
        request.setPointsUsed(100);

        OrderResponse response = orderService.createOrder(request);

        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("1.00"), response.getDiscountAmount());
        assertEquals(new BigDecimal("199.00"), response.getPaidAmount());
    }

    private OrderRequest createOrderRequest() {
        OrderRequest request = new OrderRequest();
        request.setMemberId(member.getId());
        request.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
        request.setDeliveryType("PICKUP");

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(product.getId());
        item.setQuantity(2);
        request.setItems(Collections.singletonList(item));

        return request;
    }

    private ProductRequest createOffShelfProductRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("下架商品");
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(100);
        request.setSpecification("500g");
        request.setCategory(ProductCategory.FOOD);
        request.setStatus(ProductStatus.OFF_SHELF);
        return request;
    }
}

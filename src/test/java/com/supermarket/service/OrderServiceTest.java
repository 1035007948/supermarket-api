package com.supermarket.service;

import com.supermarket.entity.Order;
import com.supermarket.entity.OrderItem;
import com.supermarket.entity.Product;
import com.supermarket.enums.OrderStatus;
import com.supermarket.enums.PaymentMethod;
import com.supermarket.enums.ProductCategory;
import com.supermarket.enums.ProductStatus;
import com.supermarket.repository.OrderRepository;
import com.supermarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setCategory(ProductCategory.FOOD);
        testProduct.setStatus(ProductStatus.ON_SALE);
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void testCreateOrder() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setPayAmount(new BigDecimal("199.98"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(2);
        item.setTotalPrice(testProduct.getPrice().multiply(new BigDecimal(2)));
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);

        assertNotNull(created.getId());
        assertEquals(OrderStatus.PENDING_PAYMENT, created.getStatus());
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setPayAmount(new BigDecimal("99.99"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(1);
        item.setTotalPrice(testProduct.getPrice());
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);
        Order found = orderService.getOrderById(created.getId());

        assertNotNull(found);
        assertEquals(created.getOrderNo(), found.getOrderNo());
    }

    @Test
    void testPayOrder() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setPayAmount(new BigDecimal("99.99"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(1);
        item.setTotalPrice(testProduct.getPrice());
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);
        Order paid = orderService.payOrder(created.getId(), PaymentMethod.ONLINE_PAYMENT.name());

        assertEquals(OrderStatus.PENDING_PREPARATION, paid.getStatus());
        assertNotNull(paid.getPayTime());
        assertEquals(PaymentMethod.ONLINE_PAYMENT, paid.getPaymentMethod());
    }

    @Test
    void testOrderStatusFlow() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setPayAmount(new BigDecimal("99.99"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(1);
        item.setTotalPrice(testProduct.getPrice());
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);

        Order paid = orderService.payOrder(created.getId(), PaymentMethod.ONLINE_PAYMENT.name());
        assertEquals(OrderStatus.PENDING_PREPARATION, paid.getStatus());

        Order prepared = orderService.prepareOrder(paid.getId());
        assertEquals(OrderStatus.PENDING_SHIPMENT, prepared.getStatus());

        Order shipped = orderService.shipOrder(prepared.getId());
        assertEquals(OrderStatus.PENDING_PICKUP, shipped.getStatus());

        Order completed = orderService.completeOrder(shipped.getId());
        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setPayAmount(new BigDecimal("99.99"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(1);
        item.setTotalPrice(testProduct.getPrice());
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);
        Order cancelled = orderService.cancelOrder(created.getId(), "测试取消");

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertNotNull(cancelled.getCancelTime());
    }

    @Test
    void testGetOrderItemsByOrderId() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setPayAmount(new BigDecimal("199.98"));

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setProductId(testProduct.getId());
        item.setProductName(testProduct.getName());
        item.setProductPrice(testProduct.getPrice());
        item.setQuantity(2);
        item.setTotalPrice(testProduct.getPrice().multiply(new BigDecimal(2)));
        orderItems.add(item);

        Order created = orderService.createOrder(order, orderItems);
        List<OrderItem> items = orderService.getOrderItemsByOrderId(created.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(testProduct.getName(), items.get(0).getProductName());
    }

    @Test
    void testPayOrderWithInvalidStatus() {
        Order order = new Order();
        order.setMemberId(1L);
        order.setTotalAmount(new BigDecimal("99.99"));
        order.setPayAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderNo("TEST123456");
        Order saved = orderRepository.save(order);

        assertThrows(IllegalStateException.class, () -> {
            orderService.payOrder(saved.getId(), PaymentMethod.ONLINE_PAYMENT.name());
        });
    }
}
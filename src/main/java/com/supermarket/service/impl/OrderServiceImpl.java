package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.BusinessException;
import com.supermarket.dto.OrderDTO;
import com.supermarket.dto.OrderItemDTO;
import com.supermarket.dto.RefundDTO;
import com.supermarket.dto.RefundReviewDTO;
import com.supermarket.entity.*;
import com.supermarket.mapper.*;
import com.supermarket.service.MemberService;
import com.supermarket.service.OrderService;
import com.supermarket.service.ProductService;
import com.supermarket.vo.OrderItemVO;
import com.supermarket.vo.OrderVO;
import com.supermarket.vo.RefundVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private RefundRecordMapper refundRecordMapper;
    @Autowired
    private MemberLevelConfigMapper memberLevelConfigMapper;

    private static final Map<String, String> STATUS_MAP = Map.of(
            "PENDING_PAYMENT", "待支付",
            "PENDING_PREPARE", "待备货",
            "PENDING_PICKUP", "待取货",
            "PENDING_SHIP", "待发货",
            "SHIPPED", "已发货",
            "COMPLETED", "已完成",
            "CANCELLED", "已取消"
    );

    private static final Map<String, String> PAY_TYPE_MAP = Map.of(
            "ONLINE", "线上支付",
            "OFFLINE", "到店付款"
    );

    private static final Map<String, String> PAY_STATUS_MAP = Map.of(
            "UNPAID", "未支付",
            "PAID", "已支付",
            "REFUNDED", "已退款"
    );

    private static final Map<String, String> DELIVERY_TYPE_MAP = Map.of(
            "PICKUP", "自提",
            "DELIVERY", "配送"
    );

    private static final Map<String, String> REFUND_STATUS_MAP = Map.of(
            "PENDING", "待审核",
            "APPROVED", "已通过",
            "REJECTED", "已拒绝"
    );

    @Override
    @Transactional
    public OrderVO createOrder(OrderDTO dto) {
        Order order = new Order();
        BeanUtils.copyProperties(dto, order);

        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus("PENDING_PAYMENT");
        order.setPayStatus("UNPAID");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (!"ON_SALE".equals(product.getStatus())) {
                throw new BusinessException("商品【" + product.getName() + "】不在售");
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new BusinessException("商品【" + product.getName() + "】库存不足");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (dto.getMemberId() != null) {
            Member member = memberMapper.selectById(dto.getMemberId());
            if (member != null) {
                MemberLevelConfig levelConfig = memberLevelConfigMapper.selectOne(
                        new LambdaQueryWrapper<MemberLevelConfig>()
                                .eq(MemberLevelConfig::getLevelCode, member.getLevel()));
                if (levelConfig != null) {
                    discountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(levelConfig.getDiscountRate()));
                }
            }
        }
        order.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));

        Integer pointsDeduct = dto.getPointsDeduct() != null ? dto.getPointsDeduct() : 0;
        order.setPointsDeduct(pointsDeduct);

        BigDecimal pointsValue = BigDecimal.valueOf(pointsDeduct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal payableAmount = totalAmount.subtract(discountAmount).subtract(pointsValue);
        order.setPayableAmount(payableAmount.max(BigDecimal.ZERO));

        save(order);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductCode(product.getCode());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setSpecification(product.getSpecification());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItemMapper.insert(orderItem);

            productService.updateStock(product.getId(), -itemDTO.getQuantity());
        }

        return getOrderById(order.getId());
    }

    @Override
    @Transactional
    public void payOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        order.setPayStatus("PAID");
        order.setPayAmount(order.getPayableAmount());
        order.setPaidTime(LocalDateTime.now());
        order.setStatus("PENDING_PREPARE");
        updateById(order);

        if (order.getMemberId() != null) {
            Member member = memberMapper.selectById(order.getMemberId());
            if (member != null) {
                BigDecimal pointsRate = BigDecimal.ONE;
                MemberLevelConfig levelConfig = memberLevelConfigMapper.selectOne(
                        new LambdaQueryWrapper<MemberLevelConfig>()
                                .eq(MemberLevelConfig::getLevelCode, member.getLevel()));
                if (levelConfig != null) {
                    pointsRate = levelConfig.getPointsRate();
                }

                int earnedPoints = order.getPayAmount().multiply(BigDecimal.valueOf(100))
                        .multiply(pointsRate).intValue();

                memberService.addPoints(order.getMemberId(), earnedPoints, "ORDER", orderId, "订单消费获得积分");

                member.setTotalConsumption(member.getTotalConsumption().add(order.getPayAmount()));
                memberMapper.updateById(member);

                if (order.getPointsDeduct() > 0) {
                    memberService.deductPoints(order.getMemberId(), order.getPointsDeduct(), "ORDER", orderId, "订单抵扣积分");
                }
            }
        }
    }

    @Override
    @Transactional
    public void prepareOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_PREPARE".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus("PENDING_PICKUP".equals(order.getDeliveryType()) ? "PENDING_PICKUP" : "PENDING_SHIP");
        order.setPreparedTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PENDING_SHIP".equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus("SHIPPED");
        order.setShippedTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!("PENDING_PICKUP".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus()))) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus("COMPLETED");
        order.setCompletedTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!("PENDING_PAYMENT".equals(order.getStatus()) || "PENDING_PREPARE".equals(order.getStatus()))) {
            throw new BusinessException("订单状态不允许取消");
        }

        order.setStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancelledTime(LocalDateTime.now());
        updateById(order);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }

        if ("PAID".equals(order.getPayStatus()) && order.getMemberId() != null) {
            int returnPoints = order.getPointsDeduct() != null ? order.getPointsDeduct() : 0;
            if (returnPoints > 0) {
                memberService.addPoints(order.getMemberId(), returnPoints, "ORDER_CANCEL", orderId, "订单取消返还积分");
            }
        }
    }

    @Override
    public Page<OrderVO> getOrderPage(Integer page, Integer size, String orderNo, String status, Long memberId) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        if (memberId != null) {
            wrapper.eq(Order::getMemberId, memberId);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = page(pageParam, wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<OrderVO> voPage = new Page<>();
        BeanUtils.copyProperties(orderPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public OrderVO getOrderById(Long id) {
        Order order = getById(id);
        if (order == null) {
            return null;
        }
        return convertToVO(order);
    }

    @Override
    @Transactional
    public RefundVO applyRefund(RefundDTO dto) {
        Order order = getById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PAID".equals(order.getPayStatus())) {
            throw new BusinessException("订单未支付，无法退款");
        }

        RefundRecord exist = refundRecordMapper.selectOne(
                new LambdaQueryWrapper<RefundRecord>()
                        .eq(RefundRecord::getOrderId, dto.getOrderId())
                        .eq(RefundRecord::getStatus, "PENDING"));
        if (exist != null) {
            throw new BusinessException("该订单已有待审核的退款申请");
        }

        RefundRecord refund = new RefundRecord();
        refund.setRefundNo(generateRefundNo());
        refund.setOrderId(order.getId());
        refund.setOrderNo(order.getOrderNo());
        refund.setMemberId(order.getMemberId());
        refund.setAmount(dto.getAmount());
        refund.setReason(dto.getReason());
        refund.setStatus("PENDING");
        refundRecordMapper.insert(refund);

        return convertToRefundVO(refund);
    }

    @Override
    @Transactional
    public void reviewRefund(RefundReviewDTO dto) {
        RefundRecord refund = refundRecordMapper.selectById(dto.getRefundId());
        if (refund == null) {
            throw new BusinessException("退款记录不存在");
        }
        if (!"PENDING".equals(refund.getStatus())) {
            throw new BusinessException("退款申请已处理");
        }

        refund.setStatus(dto.getStatus());
        refund.setReviewRemark(dto.getReviewRemark());
        refund.setReviewTime(LocalDateTime.now());
        refundRecordMapper.updateById(refund);

        if ("APPROVED".equals(dto.getStatus())) {
            Order order = getById(refund.getOrderId());
            order.setPayStatus("REFUNDED");
            updateById(order);

            if (order.getMemberId() != null) {
                memberService.rechargeBalance(order.getMemberId(), refund.getAmount(), "REFUND", refund.getId(), "订单退款");
            }
        }
    }

    private String generateOrderNo() {
        return "O" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private String generateRefundNo() {
        return "R" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusName(STATUS_MAP.getOrDefault(order.getStatus(), order.getStatus()));
        vo.setPayTypeName(PAY_TYPE_MAP.getOrDefault(order.getPayType(), order.getPayType()));
        vo.setPayStatusName(PAY_STATUS_MAP.getOrDefault(order.getPayStatus(), order.getPayStatus()));
        vo.setDeliveryTypeName(DELIVERY_TYPE_MAP.getOrDefault(order.getDeliveryType(), order.getDeliveryType()));

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        List<OrderItemVO> itemVOList = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOList);

        return vo;
    }

    private RefundVO convertToRefundVO(RefundRecord refund) {
        RefundVO vo = new RefundVO();
        BeanUtils.copyProperties(refund, vo);
        vo.setStatusName(REFUND_STATUS_MAP.getOrDefault(refund.getStatus(), refund.getStatus()));
        return vo;
    }
}

package com.supermarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.PageResult;
import com.supermarket.common.Result;
import com.supermarket.dto.OrderDTO;
import com.supermarket.dto.RefundDTO;
import com.supermarket.dto.RefundReviewDTO;
import com.supermarket.service.OrderService;
import com.supermarket.vo.OrderVO;
import com.supermarket.vo.RefundVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<OrderVO> create(@RequestBody @Valid OrderDTO dto) {
        return Result.success(orderService.createOrder(dto));
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/prepare")
    public Result<Void> prepare(@PathVariable Long id) {
        orderService.prepareOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestParam String reason) {
        orderService.cancelOrder(id, reason);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<OrderVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long memberId) {
        Page<OrderVO> result = orderService.getOrderPage(page, size, orderNo, status, memberId);
        return Result.success(PageResult.of(result.getTotal(), (long) page, (long) size, result.getRecords()));
    }

    @PostMapping("/refund")
    public Result<RefundVO> applyRefund(@RequestBody @Valid RefundDTO dto) {
        return Result.success(orderService.applyRefund(dto));
    }

    @PostMapping("/refund/review")
    public Result<Void> reviewRefund(@RequestBody @Valid RefundReviewDTO dto) {
        orderService.reviewRefund(dto);
        return Result.success();
    }
}

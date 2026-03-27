package com.supermarket.member.controller;

import com.supermarket.member.common.Result;
import com.supermarket.member.entity.BalanceRecord;
import com.supermarket.member.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @GetMapping("/records/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<List<BalanceRecord>> getMemberRecords(@PathVariable Long memberId) {
        return Result.success(balanceService.getMemberBalanceRecords(memberId));
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refund(@RequestParam Long memberId,
                               @RequestParam BigDecimal amount,
                               @RequestParam String orderNo,
                               @RequestParam(required = false) String description) {
        balanceService.refund(memberId, amount, orderNo, description);
        return Result.success();
    }
}

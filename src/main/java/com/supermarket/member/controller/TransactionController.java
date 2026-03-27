package com.supermarket.member.controller;

import com.supermarket.member.dto.ApiResponse;
import com.supermarket.member.dto.ConsumptionRequest;
import com.supermarket.member.dto.RechargeRequest;
import com.supermarket.member.entity.Transaction;
import com.supermarket.member.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/consume")
    public ResponseEntity<ApiResponse<Transaction>> consume(@Valid @RequestBody ConsumptionRequest request) {
        try {
            Transaction transaction = transactionService.consume(request);
            return ResponseEntity.ok(ApiResponse.success("消费成功", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/recharge")
    public ResponseEntity<ApiResponse<Transaction>> recharge(@Valid @RequestBody RechargeRequest request) {
        try {
            Transaction transaction = transactionService.recharge(request);
            return ResponseEntity.ok(ApiResponse.success("充值成功", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getMemberTransactions(@PathVariable Long memberId) {
        List<Transaction> transactions = transactionService.getMemberTransactions(memberId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/{transactionNo}")
    public ResponseEntity<ApiResponse<Transaction>> getTransactionByNo(@PathVariable String transactionNo) {
        return transactionService.findByTransactionNo(transactionNo)
                .map(transaction -> ResponseEntity.ok(ApiResponse.success(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }
}

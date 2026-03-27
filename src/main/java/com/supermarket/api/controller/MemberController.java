package com.supermarket.api.controller;

import com.supermarket.api.dto.*;
import com.supermarket.api.entity.Member;
import com.supermarket.api.entity.Transaction;
import com.supermarket.api.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 会员控制器
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 获取会员列表（分页）
     */
    @GetMapping
    public ApiResponse<Page<Member>> getMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return memberService.getMemberList(pageable);
    }

    /**
     * 根据ID获取会员信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    /**
     * 根据手机号获取会员信息
     */
    @GetMapping("/phone/{phone}")
    public ApiResponse<Member> getMemberByPhone(@PathVariable String phone) {
        return memberService.getMemberByPhone(phone);
    }

    /**
     * 更新会员信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    /**
     * 删除会员
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMember(@PathVariable Long id) {
        return memberService.deleteMember(id);
    }

    /**
     * 储值充值
     */
    @PostMapping("/recharge")
    public ApiResponse<Transaction> recharge(@Validated @RequestBody RechargeDTO rechargeDTO) {
        return memberService.recharge(rechargeDTO);
    }

    /**
     * 消费结算
     */
    @PostMapping("/consumption")
    public ApiResponse<Map<String, Object>> consumption(@Validated @RequestBody ConsumptionDTO consumptionDTO) {
        return memberService.consumption(consumptionDTO);
    }

    /**
     * 获取会员交易记录（分页）
     */
    @GetMapping("/{memberId}/transactions")
    public ApiResponse<Page<Transaction>> getMemberTransactions(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return memberService.getMemberTransactions(memberId, pageable);
    }

    /**
     * 获取会员统计信息
     */
    @GetMapping("/{memberId}/stats")
    public ApiResponse<Map<String, Object>> getMemberStats(@PathVariable Long memberId) {
        return memberService.getMemberStats(memberId);
    }
}

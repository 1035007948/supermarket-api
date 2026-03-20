package com.supermarket.controller;

import com.supermarket.common.ApiResponse;
import com.supermarket.dto.MemberRequest;
import com.supermarket.dto.MemberResponse;
import com.supermarket.dto.RechargeRequest;
import com.supermarket.dto.TransactionResponse;
import com.supermarket.entity.MemberLevel;
import com.supermarket.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    
    private final MemberService memberService;
    
    @PostMapping
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ApiResponse.success("会员创建成功", response);
    }
    
    @GetMapping
    public ApiResponse<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MemberResponse> members = memberService.getAllMembers(pageable);
        return ApiResponse.success(members);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getMemberById(@PathVariable Long id) {
        MemberResponse response = memberService.getMemberById(id);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/phone/{phone}")
    public ApiResponse<MemberResponse> getMemberByPhone(@PathVariable String phone) {
        MemberResponse response = memberService.getMemberByPhone(phone);
        return ApiResponse.success(response);
    }
    
    @GetMapping("/level/{level}")
    public ApiResponse<Page<MemberResponse>> getMembersByLevel(
            @PathVariable MemberLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponse> members = memberService.getMembersByLevel(level, pageable);
        return ApiResponse.success(members);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<MemberResponse> updateMember(
            @PathVariable Long id, 
            @Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ApiResponse.success("会员更新成功", response);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ApiResponse.success("会员删除成功", null);
    }
    
    @PostMapping("/{id}/recharge")
    public ApiResponse<MemberResponse> recharge(
            @PathVariable Long id,
            @Valid @RequestBody RechargeRequest request) {
        MemberResponse response = memberService.recharge(id, request);
        return ApiResponse.success("充值成功", response);
    }
    
    @PostMapping("/{id}/points/add")
    public ApiResponse<MemberResponse> addPoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        MemberResponse response = memberService.addPoints(id, points);
        return ApiResponse.success("积分添加成功", response);
    }
    
    @PostMapping("/{id}/points/use")
    public ApiResponse<MemberResponse> usePoints(
            @PathVariable Long id,
            @RequestParam Integer points) {
        MemberResponse response = memberService.usePoints(id, points);
        return ApiResponse.success("积分使用成功", response);
    }
    
    @GetMapping("/{id}/transactions")
    public ApiResponse<Page<TransactionResponse>> getMemberTransactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionResponse> transactions = memberService.getMemberTransactions(id, pageable);
        return ApiResponse.success(transactions);
    }
}

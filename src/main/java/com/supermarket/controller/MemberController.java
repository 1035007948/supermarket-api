package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.Member;
import com.supermarket.entity.StoredValueCard;
import com.supermarket.entity.StoredValueCardTransaction;
import com.supermarket.enums.MemberLevel;
import com.supermarket.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    public Result<Member> createMember(@Valid @RequestBody Member member) {
        Member createdMember = memberService.createMember(member);
        return Result.success(createdMember);
    }

    @GetMapping("/{id}")
    public Result<Member> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return Result.success(member);
    }

    @GetMapping("/phone/{phone}")
    public Result<Member> getMemberByPhone(@PathVariable String phone) {
        Member member = memberService.getMemberByPhone(phone);
        return Result.success(member);
    }

    @GetMapping
    public Result<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return Result.success(members);
    }

    @GetMapping("/page")
    public Result<Page<Member>> getMembersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Member> memberPage = memberService.getMembersPage(pageable);
        return Result.success(memberPage);
    }

    @PutMapping("/{id}")
    public Result<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        Member updatedMember = memberService.updateMember(id, member);
        return Result.success(updatedMember);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }

    @GetMapping("/level/{level}")
    public Result<List<Member>> getMembersByLevel(@PathVariable MemberLevel level) {
        List<Member> members = memberService.getMembersByLevel(level);
        return Result.success(members);
    }

    @GetMapping("/search")
    public Result<List<Member>> searchMembersByName(@RequestParam String name) {
        List<Member> members = memberService.searchMembersByName(name);
        return Result.success(members);
    }

    @PutMapping("/{id}/level/update")
    public Result<Member> updateMemberLevel(@PathVariable Long id) {
        Member member = memberService.updateMemberLevel(id);
        return Result.success(member);
    }

    @PostMapping("/{id}/discount/calculate")
    public Result<Map<String, Object>> calculateDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        Map<String, Object> discountInfo = memberService.calculateDiscount(id, amount);
        return Result.success(discountInfo);
    }

    @PutMapping("/{id}/points/add")
    public Result<Member> addPoints(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Member member = memberService.addPoints(id, amount);
        return Result.success(member);
    }

    @PutMapping("/{id}/points/deduct")
    public Result<Member> deductPoints(@PathVariable Long id, @RequestParam Integer points) {
        Member member = memberService.deductPoints(id, points);
        return Result.success(member);
    }

    @PostMapping("/{id}/points/redeem")
    public Result<BigDecimal> redeemPoints(@PathVariable Long id, @RequestParam Integer points) {
        BigDecimal amount = memberService.redeemPoints(id, points);
        return Result.success(amount);
    }

    @PostMapping("/{id}/cards")
    public Result<StoredValueCard> createStoredValueCard(@PathVariable Long id) {
        StoredValueCard card = memberService.createStoredValueCard(id);
        return Result.success(card);
    }

    @GetMapping("/cards/{cardId}")
    public Result<StoredValueCard> getStoredValueCardById(@PathVariable Long cardId) {
        StoredValueCard card = memberService.getStoredValueCardById(cardId);
        return Result.success(card);
    }

    @GetMapping("/cards/no/{cardNo}")
    public Result<StoredValueCard> getStoredValueCardByCardNo(@PathVariable String cardNo) {
        StoredValueCard card = memberService.getStoredValueCardByCardNo(cardNo);
        return Result.success(card);
    }

    @GetMapping("/{id}/cards")
    public Result<List<StoredValueCard>> getStoredValueCardsByMemberId(@PathVariable Long id) {
        List<StoredValueCard> cards = memberService.getStoredValueCardsByMemberId(id);
        return Result.success(cards);
    }

    @PutMapping("/cards/{cardId}/recharge")
    public Result<StoredValueCard> rechargeStoredValueCard(
            @PathVariable Long cardId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String remark) {
        StoredValueCard card = memberService.rechargeStoredValueCard(cardId, amount, remark);
        return Result.success(card);
    }

    @PutMapping("/cards/{cardId}/consume")
    public Result<StoredValueCard> consumeStoredValueCard(
            @PathVariable Long cardId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String remark) {
        StoredValueCard card = memberService.consumeStoredValueCard(cardId, amount, orderId, remark);
        return Result.success(card);
    }

    @GetMapping("/cards/{cardId}/transactions")
    public Result<List<StoredValueCardTransaction>> getStoredValueCardTransactionsByCardId(
            @PathVariable Long cardId) {
        List<StoredValueCardTransaction> transactions =
                memberService.getStoredValueCardTransactionsByCardId(cardId);
        return Result.success(transactions);
    }

    @GetMapping("/{id}/cards/transactions")
    public Result<List<StoredValueCardTransaction>> getStoredValueCardTransactionsByMemberId(
            @PathVariable Long id) {
        List<StoredValueCardTransaction> transactions =
                memberService.getStoredValueCardTransactionsByMemberId(id);
        return Result.success(transactions);
    }

    @PutMapping("/cards/{cardId}/activate")
    public Result<StoredValueCard> activateStoredValueCard(@PathVariable Long cardId) {
        StoredValueCard card = memberService.activateStoredValueCard(cardId);
        return Result.success(card);
    }

    @PutMapping("/cards/{cardId}/deactivate")
    public Result<StoredValueCard> deactivateStoredValueCard(@PathVariable Long cardId) {
        StoredValueCard card = memberService.deactivateStoredValueCard(cardId);
        return Result.success(card);
    }
}
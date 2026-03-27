package com.supermarket.member.controller;

import com.supermarket.member.dto.ApiResponse;
import com.supermarket.member.entity.StoredValueCard;
import com.supermarket.member.service.StoredValueCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class StoredValueCardController {

    @Autowired
    private StoredValueCardService storedValueCardService;

    @PostMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<StoredValueCard>> createCard(@PathVariable Long memberId) {
        try {
            StoredValueCard card = storedValueCardService.createCard(memberId);
            return ResponseEntity.ok(ApiResponse.success("储值卡创建成功", card));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StoredValueCard>> getCardById(@PathVariable Long id) {
        return storedValueCardService.findById(id)
                .map(card -> ResponseEntity.ok(ApiResponse.success(card)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cardNo/{cardNo}")
    public ResponseEntity<ApiResponse<StoredValueCard>> getCardByCardNo(@PathVariable String cardNo) {
        return storedValueCardService.findByCardNo(cardNo)
                .map(card -> ResponseEntity.ok(ApiResponse.success(card)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<StoredValueCard>>> getCardsByMemberId(@PathVariable Long memberId) {
        List<StoredValueCard> cards = storedValueCardService.findByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<ApiResponse<List<StoredValueCard>>> getActiveCardsByMemberId(@PathVariable Long memberId) {
        List<StoredValueCard> cards = storedValueCardService.findActiveCardsByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<StoredValueCard>> deactivateCard(@PathVariable Long id) {
        try {
            StoredValueCard card = storedValueCardService.deactivateCard(id);
            return ResponseEntity.ok(ApiResponse.success("储值卡已停用", card));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<StoredValueCard>> activateCard(@PathVariable Long id) {
        try {
            StoredValueCard card = storedValueCardService.activateCard(id);
            return ResponseEntity.ok(ApiResponse.success("储值卡已激活", card));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

package com.supermarket.dto;

import com.supermarket.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long memberId;
    private TransactionType type;
    private BigDecimal amount;
    private Integer points;
    private String description;
    private Long relatedOrderId;
    private LocalDateTime createdAt;
}

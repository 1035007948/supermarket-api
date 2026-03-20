package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BalanceRecordVO {
    private Long id;
    private Long memberId;
    private String type;
    private String typeName;
    private BigDecimal amount;
    private BigDecimal balance;
    private String source;
    private String remark;
    private LocalDateTime createTime;
}

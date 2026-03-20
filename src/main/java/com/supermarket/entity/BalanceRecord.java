package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("balance_record")
public class BalanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;
    private String source;
    private Long sourceId;
    private String remark;
    private LocalDateTime createTime;
}

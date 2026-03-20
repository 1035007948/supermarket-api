package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level_config")
public class MemberLevelConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String levelCode;
    private String levelName;
    private Integer minPoints;
    private BigDecimal minConsumption;
    private BigDecimal discountRate;
    private BigDecimal pointsRate;
    private String benefits;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

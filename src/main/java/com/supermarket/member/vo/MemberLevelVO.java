package com.supermarket.member.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberLevelVO {

    private Long id;
    private String levelName;
    private Integer levelCode;
    private Integer minPoints;
    private Integer maxPoints;
    private BigDecimal minBalance;
    private BigDecimal discountRate;
    private String benefits;
    private String description;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

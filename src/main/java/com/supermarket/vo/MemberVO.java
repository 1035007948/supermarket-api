package com.supermarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberVO {
    private Long id;
    private String memberNo;
    private String name;
    private String phone;
    private String email;
    private Integer gender;
    private String genderName;
    private LocalDate birthday;
    private String level;
    private String levelName;
    private Integer points;
    private BigDecimal balance;
    private BigDecimal totalConsumption;
    private Integer status;
    private String statusName;
    private LocalDateTime registerTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

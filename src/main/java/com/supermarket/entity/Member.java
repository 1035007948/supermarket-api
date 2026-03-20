package com.supermarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("member")
public class Member {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String memberNo;
    private String name;
    private String phone;
    private String email;
    private Integer gender;
    private LocalDate birthday;
    private String level;
    private Integer points;
    private BigDecimal balance;
    private BigDecimal totalConsumption;
    private String password;
    private Integer status;
    private LocalDateTime registerTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

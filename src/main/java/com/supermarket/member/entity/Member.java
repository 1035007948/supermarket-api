package com.supermarket.member.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
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

    private String password;

    private Integer gender;

    private LocalDateTime birthday;

    private String address;

    private Integer level;

    private BigDecimal balance;

    private Integer points;

    private Integer totalPoints;

    private BigDecimal totalConsumption;

    private LocalDateTime registerTime;

    private LocalDateTime lastConsumptionTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}

package com.supermarket.entity;

import com.supermarket.enums.MemberLevel;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberLevel level;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(name = "total_consumption", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
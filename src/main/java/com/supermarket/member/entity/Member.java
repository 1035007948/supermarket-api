package com.supermarket.member.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String memberNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String email;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MemberLevel level = MemberLevel.NORMAL;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal storedBalance = BigDecimal.ZERO;

    @Column(length = 255)
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Member(String memberNo, String name, String phone, String password) {
        this.memberNo = memberNo;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.level = MemberLevel.NORMAL;
        this.totalConsumption = BigDecimal.ZERO;
        this.points = 0;
        this.storedBalance = BigDecimal.ZERO;
    }
}

package com.supermarket.member.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class MemberDTO {

    private Long id;

    private String memberNo;

    @NotBlank(message = "会员姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;

    private String password;

    private Integer gender;

    private LocalDateTime birthday;

    private String address;

    private Integer level;

    private Integer status;
}

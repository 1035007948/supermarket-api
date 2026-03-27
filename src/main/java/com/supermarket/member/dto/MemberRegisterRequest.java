package com.supermarket.member.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MemberRegisterRequest {

    @NotBlank(message = "会员号不能为空")
    @Length(min = 6, max = 50, message = "会员号长度必须在6-50个字符之间")
    private String memberNo;

    @NotBlank(message = "姓名不能为空")
    @Length(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
}

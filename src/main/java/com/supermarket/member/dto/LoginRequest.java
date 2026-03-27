package com.supermarket.member.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "会员号不能为空")
    private String memberNo;

    @NotBlank(message = "密码不能为空")
    private String password;
}

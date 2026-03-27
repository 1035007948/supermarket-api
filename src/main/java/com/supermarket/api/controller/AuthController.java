package com.supermarket.api.controller;

import com.supermarket.api.dto.ApiResponse;
import com.supermarket.api.dto.MemberLoginDTO;
import com.supermarket.api.dto.MemberRegisterDTO;
import com.supermarket.api.entity.Member;
import com.supermarket.api.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private MemberService memberService;

    /**
     * 会员注册
     */
    @PostMapping("/register")
    public ApiResponse<Member> register(@Validated @RequestBody MemberRegisterDTO registerDTO) {
        return memberService.register(registerDTO);
    }

    /**
     * 会员登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Validated @RequestBody MemberLoginDTO loginDTO) {
        return memberService.login(loginDTO);
    }
}

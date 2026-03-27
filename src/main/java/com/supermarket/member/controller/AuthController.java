package com.supermarket.member.controller;

import com.supermarket.member.dto.ApiResponse;
import com.supermarket.member.dto.LoginRequest;
import com.supermarket.member.dto.LoginResponse;
import com.supermarket.member.dto.MemberRegisterRequest;
import com.supermarket.member.entity.Member;
import com.supermarket.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Member>> register(@Valid @RequestBody MemberRegisterRequest request) {
        try {
            Member member = memberService.register(request);
            return ResponseEntity.ok(ApiResponse.success("注册成功", member));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = memberService.login(request);
            return ResponseEntity.ok(ApiResponse.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

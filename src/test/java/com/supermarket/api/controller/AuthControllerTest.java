package com.supermarket.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.api.dto.MemberLoginDTO;
import com.supermarket.api.dto.MemberRegisterDTO;
import com.supermarket.api.entity.Member;
import com.supermarket.api.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Success() throws Exception {
        MemberRegisterDTO registerDTO = new MemberRegisterDTO();
        registerDTO.setName("测试用户");
        registerDTO.setPhone("13800138000");
        registerDTO.setPassword("123456");

        Member member = new Member();
        member.setId(1L);
        member.setName("测试用户");
        member.setPhone("13800138000");

        when(memberService.register(any(MemberRegisterDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("注册成功", member));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.name").value("测试用户"));
    }

    @Test
    void testRegister_ValidationError() throws Exception {
        MemberRegisterDTO registerDTO = new MemberRegisterDTO();
        registerDTO.setName(""); // 空名称
        registerDTO.setPhone("123"); // 无效手机号
        registerDTO.setPassword("123"); // 密码太短

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testLogin_Success() throws Exception {
        MemberLoginDTO loginDTO = new MemberLoginDTO();
        loginDTO.setPhone("13800138000");
        loginDTO.setPassword("123456");

        Map<String, Object> result = new HashMap<>();
        result.put("token", "mock-jwt-token");
        Member member = new Member();
        member.setId(1L);
        member.setName("测试用户");
        result.put("member", member);

        when(memberService.login(any(MemberLoginDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("登录成功", result));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"));
    }

    @Test
    void testLogin_ValidationError() throws Exception {
        MemberLoginDTO loginDTO = new MemberLoginDTO();
        loginDTO.setPhone(""); // 空手机号
        loginDTO.setPassword(""); // 空密码

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        MemberLoginDTO loginDTO = new MemberLoginDTO();
        loginDTO.setPhone("13800138000");
        loginDTO.setPassword("wrongpassword");

        when(memberService.login(any(MemberLoginDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.error(401, "手机号或密码错误"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk()) // 注意：这里返回200是因为我们在ApiResponse中封装了错误码
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));
    }
}

package com.supermarket.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.member.dto.ConsumeDTO;
import com.supermarket.member.dto.MemberDTO;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.enums.MemberLevelEnum;
import com.supermarket.member.enums.MemberStatusEnum;
import com.supermarket.member.service.MemberService;
import com.supermarket.member.vo.MemberVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MemberVO memberVO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();

        memberVO = new MemberVO();
        memberVO.setId(1L);
        memberVO.setMemberNo("M20240327000001");
        memberVO.setName("张三");
        memberVO.setPhone("13800138000");
        memberVO.setEmail("zhangsan@example.com");
        memberVO.setLevel(MemberLevelEnum.NORMAL.getCode());
        memberVO.setLevelName("普通会员");
        memberVO.setBalance(new BigDecimal("100.00"));
        memberVO.setPoints(100);
        memberVO.setTotalPoints(200);
        memberVO.setTotalConsumption(new BigDecimal("500.00"));
        memberVO.setStatus(MemberStatusEnum.ACTIVE.getCode());
        memberVO.setStatusName("正常");
        memberVO.setRegisterTime(LocalDateTime.now());
        memberVO.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testRegister() throws Exception {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName("张三");
        memberDTO.setPhone("13800138000");

        when(memberService.createMember(any(MemberDTO.class))).thenReturn(memberVO);

        mockMvc.perform(post("/member/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    void testCreate() throws Exception {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName("李四");
        memberDTO.setPhone("13800138001");

        when(memberService.createMember(any(MemberDTO.class))).thenReturn(memberVO);

        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testUpdate() throws Exception {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setName("张三修改");
        memberDTO.setPhone("13800138000");

        when(memberService.updateMember(anyLong(), any(MemberDTO.class))).thenReturn(memberVO);

        mockMvc.perform(put("/member/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetById() throws Exception {
        when(memberService.getMemberById(1L)).thenReturn(memberVO);

        mockMvc.perform(get("/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    void testGetByPhone() throws Exception {
        when(memberService.getMemberByPhone("13800138000")).thenReturn(memberVO);

        mockMvc.perform(get("/member/phone/13800138000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    void testList() throws Exception {
        when(memberService.listMembers()).thenReturn(Arrays.asList(memberVO));

        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("张三"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testRecharge() throws Exception {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("100.00"));
        rechargeDTO.setPaymentMethod("微信支付");

        when(memberService.recharge(any(RechargeDTO.class))).thenReturn(memberVO);

        mockMvc.perform(post("/member/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testConsume() throws Exception {
        ConsumeDTO consumeDTO = new ConsumeDTO();
        consumeDTO.setMemberId(1L);
        consumeDTO.setAmount(new BigDecimal("50.00"));
        consumeDTO.setOrderNo("ORDER202403270001");

        when(memberService.consume(any(ConsumeDTO.class))).thenReturn(memberVO);

        mockMvc.perform(post("/member/consume")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consumeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}

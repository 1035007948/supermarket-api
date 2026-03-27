package com.supermarket.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.api.dto.ConsumptionDTO;
import com.supermarket.api.dto.RechargeDTO;
import com.supermarket.api.entity.Member;
import com.supermarket.api.entity.MemberLevel;
import com.supermarket.api.entity.Transaction;
import com.supermarket.api.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 会员控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "MEMBER")
@ActiveProfiles("test")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMemberList() throws Exception {
        Member member1 = new Member();
        member1.setId(1L);
        member1.setName("会员1");
        Member member2 = new Member();
        member2.setId(2L);
        member2.setName("会员2");

        Page<Member> memberPage = new PageImpl<>(Arrays.asList(member1, member2), PageRequest.of(0, 10), 2);

        when(memberService.getMemberList(any())).thenReturn(com.supermarket.api.dto.ApiResponse.success(memberPage));

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void testGetMemberById() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setName("测试会员");
        member.setPhone("13800138000");

        when(memberService.getMemberById(1L)).thenReturn(com.supermarket.api.dto.ApiResponse.success(member));

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试会员"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    void testGetMemberById_NotFound() throws Exception {
        when(memberService.getMemberById(999L)).thenReturn(com.supermarket.api.dto.ApiResponse.error(404, "会员不存在"));

        mockMvc.perform(get("/api/members/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("会员不存在"));
    }

    @Test
    void testRecharge() throws Exception {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("1000.0"));
        rechargeDTO.setRemark("测试充值");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionNo("TXN123456");
        transaction.setAmount(1000.0);

        when(memberService.recharge(any(RechargeDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("充值成功", transaction));

        mockMvc.perform(post("/api/members/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("充值成功"));
    }

    @Test
    void testRecharge_ValidationError() throws Exception {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setAmount(new BigDecimal("-100.0")); // 无效金额

        mockMvc.perform(post("/api/members/recharge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rechargeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testConsumption() throws Exception {
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();
        consumptionDTO.setMemberId(1L);
        consumptionDTO.setAmount(new BigDecimal("100.0"));
        consumptionDTO.setUseStoredBalance(false);
        consumptionDTO.setUsePoints(0);

        Map<String, Object> result = new HashMap<>();
        result.put("originalAmount", 100.0);
        result.put("discountedAmount", 100.0);
        result.put("finalAmount", 100.0);
        result.put("earnPoints", 100);

        when(memberService.consumption(any(ConsumptionDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("消费结算成功", result));

        mockMvc.perform(post("/api/members/consumption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consumptionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("消费结算成功"))
                .andExpect(jsonPath("$.data.originalAmount").value(100.0));
    }

    @Test
    void testConsumption_WithDiscount() throws Exception {
        ConsumptionDTO consumptionDTO = new ConsumptionDTO();
        consumptionDTO.setMemberId(1L);
        consumptionDTO.setAmount(new BigDecimal("100.0"));

        Map<String, Object> result = new HashMap<>();
        result.put("originalAmount", 100.0);
        result.put("discountedAmount", 90.0);
        result.put("discountAmount", 10.0);
        result.put("discountRate", 0.1);
        result.put("finalAmount", 90.0);
        result.put("earnPoints", 135);

        when(memberService.consumption(any(ConsumptionDTO.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("消费结算成功", result));

        mockMvc.perform(post("/api/members/consumption")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(consumptionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.discountAmount").value(10.0))
                .andExpect(jsonPath("$.data.discountRate").value(0.1));
    }

    @Test
    void testGetMemberStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("memberId", 1L);
        stats.put("name", "测试会员");
        stats.put("level", "GOLD");
        stats.put("points", 2000);
        stats.put("storedBalance", 10000.0);
        stats.put("nextLevelName", "已达最高等级");

        when(memberService.getMemberStats(1L)).thenReturn(com.supermarket.api.dto.ApiResponse.success(stats));

        mockMvc.perform(get("/api/members/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.level").value("GOLD"))
                .andExpect(jsonPath("$.data.points").value(2000));
    }

    @Test
    void testGetMemberTransactions() throws Exception {
        Transaction tx1 = new Transaction();
        tx1.setId(1L);
        tx1.setType("RECHARGE");
        tx1.setAmount(1000.0);

        Transaction tx2 = new Transaction();
        tx2.setId(2L);
        tx2.setType("CONSUMPTION");
        tx2.setAmount(100.0);

        Page<Transaction> transactionPage = new PageImpl<>(Arrays.asList(tx1, tx2), PageRequest.of(0, 10), 2);

        when(memberService.getMemberTransactions(eq(1L), any()))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success(transactionPage));

        mockMvc.perform(get("/api/members/1/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void testUpdateMember() throws Exception {
        Member member = new Member();
        member.setName("更新后的名字");
        member.setPhone("13800138999");
        member.setStatus(1);

        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName("更新后的名字");
        updatedMember.setPhone("13800138999");

        when(memberService.updateMember(eq(1L), any(Member.class)))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("更新成功", updatedMember));

        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteMember() throws Exception {
        when(memberService.deleteMember(1L))
                .thenReturn(com.supermarket.api.dto.ApiResponse.success("删除成功", null));

        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }
}

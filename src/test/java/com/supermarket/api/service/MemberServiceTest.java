package com.supermarket.api.service;

import com.supermarket.api.dto.*;
import com.supermarket.api.entity.Member;
import com.supermarket.api.entity.MemberLevel;
import com.supermarket.api.entity.Transaction;
import com.supermarket.api.repository.MemberRepository;
import com.supermarket.api.repository.TransactionRepository;
import com.supermarket.api.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 会员服务测试类
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private MemberRegisterDTO registerDTO;
    private MemberLoginDTO loginDTO;
    private RechargeDTO rechargeDTO;
    private ConsumptionDTO consumptionDTO;

    @BeforeEach
    void setUp() {
        testMember = new Member();
        testMember.setId(1L);
        testMember.setName("测试会员");
        testMember.setPhone("13800138000");
        testMember.setCardNumber("VIP20240101000001");
        testMember.setPassword("encodedPassword");
        testMember.setLevel(MemberLevel.REGULAR);
        testMember.setPoints(100);
        testMember.setStoredBalance(500.0);
        testMember.setStatus(1);

        registerDTO = new MemberRegisterDTO();
        registerDTO.setName("新会员");
        registerDTO.setPhone("13800138001");
        registerDTO.setPassword("123456");

        loginDTO = new MemberLoginDTO();
        loginDTO.setPhone("13800138000");
        loginDTO.setPassword("123456");

        rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("1000.0"));
        rechargeDTO.setRemark("测试充值");

        consumptionDTO = new ConsumptionDTO();
        consumptionDTO.setMemberId(1L);
        consumptionDTO.setAmount(new BigDecimal("100.0"));
        consumptionDTO.setUseStoredBalance(false);
        consumptionDTO.setUsePoints(0);
    }

    @Test
    void testRegister_Success() {
        when(memberRepository.existsByPhone(registerDTO.getPhone())).thenReturn(false);
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);

        ApiResponse<Member> response = memberService.register(registerDTO);

        assertEquals(200, response.getCode());
        assertEquals("注册成功", response.getMessage());
        assertNotNull(response.getData());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testRegister_PhoneAlreadyExists() {
        when(memberRepository.existsByPhone(registerDTO.getPhone())).thenReturn(true);

        ApiResponse<Member> response = memberService.register(registerDTO);

        assertEquals(400, response.getCode());
        assertEquals("该手机号已注册", response.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testGetMemberById_Found() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        ApiResponse<Member> response = memberService.getMemberById(1L);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("测试会员", response.getData().getName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse<Member> response = memberService.getMemberById(999L);

        assertEquals(404, response.getCode());
        assertEquals("会员不存在", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testGetMemberList() {
        List<Member> members = Arrays.asList(testMember, new Member());
        Page<Member> memberPage = new PageImpl<>(members);
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.findAll(pageable)).thenReturn(memberPage);

        ApiResponse<Page<Member>> response = memberService.getMemberList(pageable);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getContent().size());
    }

    @Test
    void testRecharge_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        ApiResponse<Transaction> response = memberService.recharge(rechargeDTO);

        assertEquals(200, response.getCode());
        assertEquals("充值成功", response.getMessage());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testRecharge_MemberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        rechargeDTO.setMemberId(999L);

        ApiResponse<Transaction> response = memberService.recharge(rechargeDTO);

        assertEquals(404, response.getCode());
        assertEquals("会员不存在", response.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testConsumption_Success_RegularMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        ApiResponse<Map<String, Object>> response = memberService.consumption(consumptionDTO);

        assertEquals(200, response.getCode());
        assertEquals("消费结算成功", response.getMessage());
        Map<String, Object> result = response.getData();
        assertNotNull(result);
        assertEquals(100.0, result.get("originalAmount"));
        assertEquals(100.0, result.get("discountedAmount"));
        assertEquals(100, result.get("earnPoints"));
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testConsumption_Success_GoldMemberWithDiscount() {
        testMember.setLevel(MemberLevel.GOLD);
        testMember.setPoints(200);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        ApiResponse<Map<String, Object>> response = memberService.consumption(consumptionDTO);

        assertEquals(200, response.getCode());
        Map<String, Object> result = response.getData();
        assertNotNull(result);
        assertEquals(100.0, result.get("originalAmount"));
        assertEquals(90.0, result.get("discountedAmount"));
        assertEquals(10.0, result.get("discountAmount"));
        assertEquals(0.1, result.get("discountRate"));
        assertEquals(135, result.get("earnPoints")); // 90 * 1.5
    }

    @Test
    void testConsumption_WithPointsDeduction() {
        testMember.setPoints(200);
        consumptionDTO.setUsePoints(100); // 100积分抵扣1元
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(testMember);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        ApiResponse<Map<String, Object>> response = memberService.consumption(consumptionDTO);

        assertEquals(200, response.getCode());
        Map<String, Object> result = response.getData();
        assertNotNull(result);
        assertEquals(100, result.get("usePoints"));
        assertEquals(1.0, result.get("pointsDeductionAmount"));
        assertEquals(99.0, result.get("finalAmount"));
    }

    @Test
    void testConsumption_MemberNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        consumptionDTO.setMemberId(999L);

        ApiResponse<Map<String, Object>> response = memberService.consumption(consumptionDTO);

        assertEquals(404, response.getCode());
        assertEquals("会员不存在", response.getMessage());
    }

    @Test
    void testConsumption_InsufficientBalance() {
        testMember.setStoredBalance(50.0);
        consumptionDTO.setUseStoredBalance(true);
        consumptionDTO.setAmount(new BigDecimal("100.0"));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        ApiResponse<Map<String, Object>> response = memberService.consumption(consumptionDTO);

        assertEquals(400, response.getCode());
        assertEquals("储值余额不足", response.getMessage());
    }

    @Test
    void testGetMemberStats() {
        testMember.setTotalRecharge(500.0);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        ApiResponse<Map<String, Object>> response = memberService.getMemberStats(1L);

        assertEquals(200, response.getCode());
        Map<String, Object> stats = response.getData();
        assertNotNull(stats);
        assertEquals(1L, stats.get("memberId"));
        assertEquals("测试会员", stats.get("name"));
        assertEquals("REGULAR", stats.get("level"));
        assertEquals(100, stats.get("points"));
        assertEquals(500.0, stats.get("storedBalance"));
        assertEquals(500.0, stats.get("totalRecharge"));
        assertEquals("银卡会员", stats.get("nextLevelName"));
        assertEquals(500.0, stats.get("nextLevelNeedRecharge"));
    }

    @Test
    void testGetMemberStats_GoldMember() {
        testMember.setLevel(MemberLevel.GOLD);
        testMember.setTotalRecharge(10000.0);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));

        ApiResponse<Map<String, Object>> response = memberService.getMemberStats(1L);

        assertEquals(200, response.getCode());
        Map<String, Object> stats = response.getData();
        assertNotNull(stats);
        assertEquals("GOLD", stats.get("level"));
        assertEquals(10000.0, stats.get("totalRecharge"));
        assertEquals("已达最高等级", stats.get("nextLevelName"));
        assertEquals(0.0, stats.get("nextLevelNeedRecharge"));
    }

    @Test
    void testUpdateMember_Success() {
        Member updatedMember = new Member();
        updatedMember.setName("更新后的名字");
        updatedMember.setPhone("13800138999");
        updatedMember.setStatus(1);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
        when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

        ApiResponse<Member> response = memberService.updateMember(1L, updatedMember);

        assertEquals(200, response.getCode());
        assertEquals("更新成功", response.getMessage());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void testUpdateMember_NotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        ApiResponse<Member> response = memberService.updateMember(999L, new Member());

        assertEquals(404, response.getCode());
        assertEquals("会员不存在", response.getMessage());
    }

    @Test
    void testDeleteMember_Success() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(1L);

        ApiResponse<Void> response = memberService.deleteMember(1L);

        assertEquals(200, response.getCode());
        assertEquals("删除成功", response.getMessage());
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMember_NotFound() {
        when(memberRepository.existsById(999L)).thenReturn(false);

        ApiResponse<Void> response = memberService.deleteMember(999L);

        assertEquals(404, response.getCode());
        assertEquals("会员不存在", response.getMessage());
    }

    @Test
    void testGetMemberTransactions() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        Page<Transaction> transactionPage = new PageImpl<>(transactions);
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findByMemberIdOrderByCreateTimeDesc(eq(1L), any(Pageable.class))).thenReturn(transactionPage);

        ApiResponse<Page<Transaction>> response = memberService.getMemberTransactions(1L, pageable);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getContent().size());
    }
}

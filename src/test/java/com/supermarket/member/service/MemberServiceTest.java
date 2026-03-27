package com.supermarket.member.service;

import com.supermarket.member.dto.ConsumeDTO;
import com.supermarket.member.dto.MemberDTO;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.enums.MemberLevelEnum;
import com.supermarket.member.enums.MemberStatusEnum;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.service.impl.MemberServiceImpl;
import com.supermarket.member.vo.MemberVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PointsService pointsService;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberDTO memberDTO;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setMemberNo("M20240327000001");
        member.setName("张三");
        member.setPhone("13800138000");
        member.setEmail("zhangsan@example.com");
        member.setLevel(MemberLevelEnum.NORMAL.getCode());
        member.setBalance(new BigDecimal("100.00"));
        member.setPoints(100);
        member.setTotalPoints(200);
        member.setTotalConsumption(new BigDecimal("500.00"));
        member.setStatus(MemberStatusEnum.ACTIVE.getCode());
        member.setRegisterTime(LocalDateTime.now());

        memberDTO = new MemberDTO();
        memberDTO.setName("张三");
        memberDTO.setPhone("13800138000");
        memberDTO.setEmail("zhangsan@example.com");
    }

    @Test
    void testCreateMember() {
        when(memberMapper.selectByPhone(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberMapper.insert(any(Member.class))).thenReturn(1);

        MemberVO result = memberService.createMember(memberDTO);

        assertNotNull(result);
        assertEquals("张三", result.getName());
        assertEquals("13800138000", result.getPhone());
        verify(memberMapper, times(1)).insert(any(Member.class));
    }

    @Test
    void testCreateMember_PhoneExists() {
        when(memberMapper.selectByPhone(anyString())).thenReturn(member);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.createMember(memberDTO);
        });

        assertEquals("该手机号已注册会员", exception.getMessage());
    }

    @Test
    void testGetMemberById() {
        when(memberMapper.selectById(1L)).thenReturn(member);

        MemberVO result = memberService.getMemberById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张三", result.getName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.getMemberById(1L);
        });

        assertEquals("会员不存在", exception.getMessage());
    }

    @Test
    void testGetMemberByPhone() {
        when(memberMapper.selectByPhone("13800138000")).thenReturn(member);

        MemberVO result = memberService.getMemberByPhone("13800138000");

        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
    }

    @Test
    void testListMembers() {
        when(memberMapper.selectList(null)).thenReturn(Arrays.asList(member));

        List<MemberVO> result = memberService.listMembers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getName());
    }

    @Test
    void testDeleteMember() {
        when(memberMapper.selectById(1L)).thenReturn(member);
        when(memberMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> memberService.deleteMember(1L));
        verify(memberMapper, times(1)).deleteById(1L);
    }

    @Test
    void testRecharge() {
        RechargeDTO rechargeDTO = new RechargeDTO();
        rechargeDTO.setMemberId(1L);
        rechargeDTO.setAmount(new BigDecimal("100.00"));
        rechargeDTO.setPaymentMethod("微信支付");

        when(memberMapper.selectById(1L)).thenReturn(member);
        doNothing().when(balanceService).recharge(any(RechargeDTO.class));
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        MemberVO result = memberService.recharge(rechargeDTO);

        assertNotNull(result);
        verify(balanceService, times(1)).recharge(any(RechargeDTO.class));
    }

    @Test
    void testConsume() {
        ConsumeDTO consumeDTO = new ConsumeDTO();
        consumeDTO.setMemberId(1L);
        consumeDTO.setAmount(new BigDecimal("50.00"));
        consumeDTO.setOrderNo("ORDER202403270001");
        consumeDTO.setUsePoints(0);

        when(memberMapper.selectById(1L)).thenReturn(member);
        doNothing().when(balanceService).consume(anyLong(), any(BigDecimal.class), anyString(), any());
        when(pointsService.calculatePointsByAmount(any(BigDecimal.class))).thenReturn(5);
        doNothing().when(pointsService).earnPoints(anyLong(), anyInt(), anyString(), anyString(), anyString());
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        MemberVO result = memberService.consume(consumeDTO);

        assertNotNull(result);
        verify(balanceService, times(1)).consume(anyLong(), any(BigDecimal.class), anyString(), any());
    }

    @Test
    void testConsume_InsufficientBalance() {
        member.setBalance(new BigDecimal("10.00"));
        
        ConsumeDTO consumeDTO = new ConsumeDTO();
        consumeDTO.setMemberId(1L);
        consumeDTO.setAmount(new BigDecimal("100.00"));
        consumeDTO.setOrderNo("ORDER202403270001");

        when(memberMapper.selectById(1L)).thenReturn(member);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            memberService.consume(consumeDTO);
        });

        assertEquals("余额不足", exception.getMessage());
    }

    @Test
    void testGenerateMemberNo() {
        when(memberMapper.selectCount()).thenReturn(0L);

        String memberNo = memberService.generateMemberNo();

        assertNotNull(memberNo);
        assertTrue(memberNo.startsWith("M"));
        assertEquals(15, memberNo.length());
    }
}

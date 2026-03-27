package com.supermarket.member.service;

import com.supermarket.member.dto.LoginRequest;
import com.supermarket.member.dto.LoginResponse;
import com.supermarket.member.dto.MemberRegisterRequest;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberLevel;
import com.supermarket.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MemberRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new MemberRegisterRequest();
        registerRequest.setMemberNo("TEST001");
        registerRequest.setName("测试会员");
        registerRequest.setPhone("13800138000");
        registerRequest.setPassword("password123");
    }

    @Test
    void testRegister() {
        Member member = memberService.register(registerRequest);

        assertNotNull(member.getId());
        assertEquals("TEST001", member.getMemberNo());
        assertEquals("测试会员", member.getName());
        assertEquals("13800138000", member.getPhone());
        assertEquals(MemberLevel.NORMAL, member.getLevel());
        assertTrue(passwordEncoder.matches("password123", member.getPassword()));
    }

    @Test
    void testRegisterDuplicateMemberNo() {
        memberService.register(registerRequest);

        MemberRegisterRequest duplicateRequest = new MemberRegisterRequest();
        duplicateRequest.setMemberNo("TEST001");
        duplicateRequest.setName("另一个会员");
        duplicateRequest.setPhone("13900139000");
        duplicateRequest.setPassword("password456");

        assertThrows(RuntimeException.class, () -> memberService.register(duplicateRequest));
    }

    @Test
    void testLogin() {
        memberService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMemberNo("TEST001");
        loginRequest.setPassword("password123");

        LoginResponse response = memberService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("TEST001", response.getMemberNo());
        assertEquals("测试会员", response.getName());
        assertEquals("普通会员", response.getLevel());
    }

    @Test
    void testLoginWithWrongPassword() {
        memberService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMemberNo("TEST001");
        loginRequest.setPassword("wrongpassword");

        assertThrows(RuntimeException.class, () -> memberService.login(loginRequest));
    }

    @Test
    void testUpdateMemberLevel() {
        Member member = memberService.register(registerRequest);
        member.setTotalConsumption(new java.math.BigDecimal("1500"));

        memberService.updateMemberLevel(member);

        Member updatedMember = memberRepository.findById(member.getId()).orElse(null);
        assertNotNull(updatedMember);
        assertEquals(MemberLevel.SILVER, updatedMember.getLevel());
    }
}

package com.supermarket.member.service;

import com.supermarket.member.dto.LoginRequest;
import com.supermarket.member.dto.LoginResponse;
import com.supermarket.member.dto.MemberRegisterRequest;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberLevel;
import com.supermarket.member.repository.MemberRepository;
import com.supermarket.member.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Member register(MemberRegisterRequest request) {
        if (memberRepository.existsByMemberNo(request.getMemberNo())) {
            throw new RuntimeException("会员号已存在");
        }

        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        Member member = new Member();
        member.setMemberNo(request.getMemberNo());
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setLevel(MemberLevel.NORMAL);
        member.setTotalConsumption(java.math.BigDecimal.ZERO);
        member.setPoints(0);
        member.setStoredBalance(java.math.BigDecimal.ZERO);

        return memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Member> memberOpt = memberRepository.findByMemberNo(request.getMemberNo());

        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }

        Member member = memberOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String token = jwtTokenUtil.generateToken(member.getMemberNo());

        return new LoginResponse(
                token,
                member.getMemberNo(),
                member.getName(),
                member.getLevel().getName()
        );
    }

    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByMemberNo(String memberNo) {
        return memberRepository.findByMemberNo(memberNo);
    }

    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Member> findByLevel(MemberLevel level) {
        return memberRepository.findByLevel(level);
    }

    public Member updateMember(Member member) {
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public void updateMemberLevel(Member member) {
        java.math.BigDecimal totalConsumption = member.getTotalConsumption();

        if (totalConsumption.compareTo(new java.math.BigDecimal("5000")) >= 0) {
            member.setLevel(MemberLevel.GOLD);
        } else if (totalConsumption.compareTo(new java.math.BigDecimal("1000")) >= 0) {
            member.setLevel(MemberLevel.SILVER);
        } else {
            member.setLevel(MemberLevel.NORMAL);
        }

        memberRepository.save(member);
    }
}

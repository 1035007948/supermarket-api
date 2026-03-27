package com.supermarket.member.controller;

import com.supermarket.member.common.Result;
import com.supermarket.member.dto.ConsumeDTO;
import com.supermarket.member.dto.MemberDTO;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.service.MemberService;
import com.supermarket.member.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/register")
    public Result<MemberVO> register(@Valid @RequestBody MemberDTO memberDTO) {
        return Result.success(memberService.createMember(memberDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MemberVO> create(@Valid @RequestBody MemberDTO memberDTO) {
        return Result.success(memberService.createMember(memberDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MemberVO> update(@PathVariable Long id, @Valid @RequestBody MemberDTO memberDTO) {
        return Result.success(memberService.updateMember(id, memberDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<MemberVO> getById(@PathVariable Long id) {
        return Result.success(memberService.getMemberById(id));
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<MemberVO> getByPhone(@PathVariable String phone) {
        return Result.success(memberService.getMemberByPhone(phone));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<List<MemberVO>> list() {
        return Result.success(memberService.listMembers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return Result.success();
    }

    @PostMapping("/recharge")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<MemberVO> recharge(@Valid @RequestBody RechargeDTO rechargeDTO) {
        return Result.success(memberService.recharge(rechargeDTO));
    }

    @PostMapping("/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Result<MemberVO> consume(@Valid @RequestBody ConsumeDTO consumeDTO) {
        return Result.success(memberService.consume(consumeDTO));
    }
}

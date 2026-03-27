package com.supermarket.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.dto.ConsumeDTO;
import com.supermarket.member.dto.MemberDTO;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.vo.MemberVO;

import java.util.List;

public interface MemberService extends IService<Member> {

    MemberVO createMember(MemberDTO memberDTO);

    MemberVO updateMember(Long id, MemberDTO memberDTO);

    MemberVO getMemberById(Long id);

    MemberVO getMemberByPhone(String phone);

    List<MemberVO> listMembers();

    void deleteMember(Long id);

    MemberVO recharge(RechargeDTO rechargeDTO);

    MemberVO consume(ConsumeDTO consumeDTO);

    void updateMemberLevel(Long memberId);

    String generateMemberNo();
}

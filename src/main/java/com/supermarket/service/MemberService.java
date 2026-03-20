package com.supermarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.dto.MemberDTO;
import com.supermarket.entity.Member;
import com.supermarket.vo.BalanceRecordVO;
import com.supermarket.vo.MemberVO;
import com.supermarket.vo.PointsRecordVO;

import java.math.BigDecimal;
import java.util.List;

public interface MemberService extends IService<Member> {

    MemberVO createMember(MemberDTO dto);

    void updateMember(MemberDTO dto);

    void deleteMember(Long id);

    MemberVO getMemberById(Long id);

    MemberVO getMemberByPhone(String phone);

    Page<MemberVO> getMemberPage(Integer page, Integer size, String name, String phone, String level);

    List<MemberVO> getMemberList();

    void rechargeBalance(Long memberId, BigDecimal amount, String source, Long sourceId, String remark);

    void consumeBalance(Long memberId, BigDecimal amount, String source, Long sourceId, String remark);

    void addPoints(Long memberId, Integer points, String source, Long sourceId, String remark);

    void deductPoints(Long memberId, Integer points, String source, Long sourceId, String remark);

    Page<PointsRecordVO> getPointsRecordPage(Long memberId, Integer page, Integer size);

    Page<BalanceRecordVO> getBalanceRecordPage(Long memberId, Integer page, Integer size);
}

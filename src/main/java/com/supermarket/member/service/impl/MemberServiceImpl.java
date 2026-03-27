package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.member.dto.ConsumeDTO;
import com.supermarket.member.dto.MemberDTO;
import com.supermarket.member.dto.RechargeDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.enums.MemberLevelEnum;
import com.supermarket.member.enums.MemberStatusEnum;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.service.BalanceService;
import com.supermarket.member.service.MemberService;
import com.supermarket.member.service.PointsService;
import com.supermarket.member.vo.MemberVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private BalanceService balanceService;

    @Override
    @Transactional
    public MemberVO createMember(MemberDTO memberDTO) {
        Member existingMember = baseMapper.selectByPhone(memberDTO.getPhone());
        if (existingMember != null) {
            throw new RuntimeException("该手机号已注册会员");
        }

        Member member = new Member();
        BeanUtils.copyProperties(memberDTO, member);
        member.setMemberNo(generateMemberNo());
        member.setLevel(MemberLevelEnum.NORMAL.getCode());
        member.setBalance(BigDecimal.ZERO);
        member.setPoints(0);
        member.setTotalPoints(0);
        member.setTotalConsumption(BigDecimal.ZERO);
        member.setRegisterTime(LocalDateTime.now());
        member.setStatus(MemberStatusEnum.ACTIVE.getCode());

        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        baseMapper.insert(member);
        return convertToVO(member);
    }

    @Override
    @Transactional
    public MemberVO updateMember(Long id, MemberDTO memberDTO) {
        Member member = baseMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        if (!member.getPhone().equals(memberDTO.getPhone())) {
            Member existingMember = baseMapper.selectByPhone(memberDTO.getPhone());
            if (existingMember != null) {
                throw new RuntimeException("该手机号已被其他会员使用");
            }
        }

        BeanUtils.copyProperties(memberDTO, member, "id", "memberNo", "password", "balance", "points", "totalPoints", "totalConsumption", "registerTime", "level");

        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        baseMapper.updateById(member);
        return convertToVO(member);
    }

    @Override
    public MemberVO getMemberById(Long id) {
        Member member = baseMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        return convertToVO(member);
    }

    @Override
    public MemberVO getMemberByPhone(String phone) {
        Member member = baseMapper.selectByPhone(phone);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        return convertToVO(member);
    }

    @Override
    public List<MemberVO> listMembers() {
        List<Member> members = baseMapper.selectList(null);
        return members.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = baseMapper.selectById(id);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public MemberVO recharge(RechargeDTO rechargeDTO) {
        Member member = baseMapper.selectById(rechargeDTO.getMemberId());
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        balanceService.recharge(rechargeDTO);

        member.setBalance(member.getBalance().add(rechargeDTO.getAmount()));
        baseMapper.updateById(member);

        updateMemberLevel(rechargeDTO.getMemberId());

        return convertToVO(baseMapper.selectById(rechargeDTO.getMemberId()));
    }

    @Override
    @Transactional
    public MemberVO consume(ConsumeDTO consumeDTO) {
        Member member = baseMapper.selectById(consumeDTO.getMemberId());
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        if (member.getStatus() != MemberStatusEnum.ACTIVE.getCode()) {
            throw new RuntimeException("会员状态异常，无法消费");
        }

        BigDecimal actualAmount = consumeDTO.getAmount();
        Integer pointsUsed = consumeDTO.getUsePoints() != null ? consumeDTO.getUsePoints() : 0;

        if (pointsUsed > 0) {
            if (member.getPoints() < pointsUsed) {
                throw new RuntimeException("积分不足");
            }
            BigDecimal pointsDiscount = new BigDecimal(pointsUsed).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            actualAmount = actualAmount.subtract(pointsDiscount);
            if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
                actualAmount = BigDecimal.ZERO;
            }
        }

        MemberLevelEnum levelEnum = MemberLevelEnum.getByCode(member.getLevel());
        if (levelEnum != null) {
            BigDecimal discountRate = new BigDecimal(levelEnum.getDiscountRate());
            actualAmount = actualAmount.multiply(discountRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        if (member.getBalance().compareTo(actualAmount) < 0) {
            throw new RuntimeException("余额不足");
        }

        balanceService.consume(consumeDTO.getMemberId(), actualAmount, consumeDTO.getOrderNo(), consumeDTO.getDescription());

        if (pointsUsed > 0) {
            pointsService.deductPoints(consumeDTO.getMemberId(), pointsUsed, "消费抵扣", consumeDTO.getOrderNo(), "消费抵扣积分");
        }

        Integer earnedPoints = pointsService.calculatePointsByAmount(consumeDTO.getAmount());
        if (earnedPoints > 0) {
            pointsService.earnPoints(consumeDTO.getMemberId(), earnedPoints, "消费获得", consumeDTO.getOrderNo(), "消费获得积分");
        }

        member.setBalance(member.getBalance().subtract(actualAmount));
        member.setTotalConsumption(member.getTotalConsumption().add(consumeDTO.getAmount()));
        member.setLastConsumptionTime(LocalDateTime.now());
        baseMapper.updateById(member);

        updateMemberLevel(consumeDTO.getMemberId());

        return convertToVO(baseMapper.selectById(consumeDTO.getMemberId()));
    }

    @Override
    @Transactional
    public void updateMemberLevel(Long memberId) {
        Member member = baseMapper.selectById(memberId);
        if (member == null) {
            return;
        }

        MemberLevelEnum newLevel = MemberLevelEnum.getByPoints(member.getTotalPoints());
        if (newLevel != null && !newLevel.getCode().equals(member.getLevel())) {
            member.setLevel(newLevel.getCode());
            baseMapper.updateById(member);
        }
    }

    @Override
    public String generateMemberNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = baseMapper.selectCount();
        return "M" + dateStr + String.format("%06d", count + 1);
    }

    private MemberVO convertToVO(Member member) {
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);

        MemberLevelEnum levelEnum = MemberLevelEnum.getByCode(member.getLevel());
        if (levelEnum != null) {
            vo.setLevelName(levelEnum.getName());
        }

        MemberStatusEnum statusEnum = MemberStatusEnum.getByCode(member.getStatus());
        if (statusEnum != null) {
            vo.setStatusName(statusEnum.getName());
        }

        return vo;
    }
}

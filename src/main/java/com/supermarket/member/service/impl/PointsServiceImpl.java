package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.member.dto.PointsOperateDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.PointsRecord;
import com.supermarket.member.enums.PointsTypeEnum;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.mapper.PointsRecordMapper;
import com.supermarket.member.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PointsServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsService {

    @Autowired
    private MemberMapper memberMapper;

    private static final BigDecimal POINTS_RATE = new BigDecimal("0.1");
    private static final int POINTS_PER_YUAN = 1;

    @Override
    @Transactional
    public void earnPoints(Long memberId, Integer points, String source, String orderNo, String description) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        PointsRecord record = new PointsRecord();
        record.setMemberId(memberId);
        record.setType(PointsTypeEnum.EARN.getCode());
        record.setPoints(points);
        record.setBeforePoints(member.getPoints());
        record.setAfterPoints(member.getPoints() + points);
        record.setSource(source);
        record.setOrderNo(orderNo);
        record.setDescription(description);

        baseMapper.insert(record);

        member.setPoints(member.getPoints() + points);
        member.setTotalPoints(member.getTotalPoints() + points);
        memberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void deductPoints(Long memberId, Integer points, String source, String orderNo, String description) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        if (member.getPoints() < points) {
            throw new RuntimeException("积分不足");
        }

        PointsRecord record = new PointsRecord();
        record.setMemberId(memberId);
        record.setType(PointsTypeEnum.DEDUCT.getCode());
        record.setPoints(-points);
        record.setBeforePoints(member.getPoints());
        record.setAfterPoints(member.getPoints() - points);
        record.setSource(source);
        record.setOrderNo(orderNo);
        record.setDescription(description);

        baseMapper.insert(record);

        member.setPoints(member.getPoints() - points);
        memberMapper.updateById(member);
    }

    @Override
    @Transactional
    public PointsRecord operatePoints(PointsOperateDTO operateDTO) {
        if (PointsTypeEnum.EARN.getCode().equals(operateDTO.getType())) {
            earnPoints(operateDTO.getMemberId(), operateDTO.getPoints(), operateDTO.getSource(), operateDTO.getOrderNo(), operateDTO.getDescription());
        } else if (PointsTypeEnum.DEDUCT.getCode().equals(operateDTO.getType())) {
            deductPoints(operateDTO.getMemberId(), operateDTO.getPoints(), operateDTO.getSource(), operateDTO.getOrderNo(), operateDTO.getDescription());
        } else {
            throw new RuntimeException("不支持的积分操作类型");
        }

        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", operateDTO.getMemberId());
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 1");
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<PointsRecord> getMemberPointsRecords(Long memberId) {
        return baseMapper.selectByMemberId(memberId);
    }

    @Override
    public Integer calculatePointsByAmount(BigDecimal amount) {
        return amount.multiply(POINTS_RATE).setScale(0, RoundingMode.FLOOR).intValue();
    }
}

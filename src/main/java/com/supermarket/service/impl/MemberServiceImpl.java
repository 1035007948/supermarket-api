package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.common.BusinessException;
import com.supermarket.dto.MemberDTO;
import com.supermarket.entity.BalanceRecord;
import com.supermarket.entity.Member;
import com.supermarket.entity.MemberLevelConfig;
import com.supermarket.entity.PointsRecord;
import com.supermarket.mapper.BalanceRecordMapper;
import com.supermarket.mapper.MemberLevelConfigMapper;
import com.supermarket.mapper.MemberMapper;
import com.supermarket.mapper.PointsRecordMapper;
import com.supermarket.service.MemberService;
import com.supermarket.vo.BalanceRecordVO;
import com.supermarket.vo.MemberVO;
import com.supermarket.vo.PointsRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    @Autowired
    private PointsRecordMapper pointsRecordMapper;
    @Autowired
    private BalanceRecordMapper balanceRecordMapper;
    @Autowired
    private MemberLevelConfigMapper memberLevelConfigMapper;

    private static final Map<String, String> LEVEL_MAP = Map.of(
            "NORMAL", "普通会员",
            "SILVER", "银卡会员",
            "GOLD", "金卡会员"
    );

    private static final Map<Integer, String> GENDER_MAP = Map.of(
            0, "未知",
            1, "男",
            2, "女"
    );

    private static final Map<String, String> POINTS_TYPE_MAP = Map.of(
            "EARN", "获得",
            "DEDUCT", "抵扣",
            "EXPIRE", "过期"
    );

    private static final Map<String, String> BALANCE_TYPE_MAP = Map.of(
            "RECHARGE", "充值",
            "CONSUME", "消费",
            "REFUND", "退款"
    );

    @Override
    @Transactional
    public MemberVO createMember(MemberDTO dto) {
        Member exist = getOne(new LambdaQueryWrapper<Member>().eq(Member::getPhone, dto.getPhone()));
        if (exist != null) {
            throw new BusinessException("手机号已注册");
        }

        Member member = new Member();
        BeanUtils.copyProperties(dto, member);
        member.setMemberNo(generateMemberNo());
        member.setLevel("NORMAL");
        member.setPoints(0);
        member.setBalance(BigDecimal.ZERO);
        member.setTotalConsumption(BigDecimal.ZERO);
        member.setStatus(1);

        if (StringUtils.hasText(dto.getPassword())) {
            member.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        }

        save(member);
        return convertToVO(member);
    }

    @Override
    @Transactional
    public void updateMember(MemberDTO dto) {
        Member member = getById(dto.getId());
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        Member exist = getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getPhone, dto.getPhone())
                .ne(Member::getId, dto.getId()));
        if (exist != null) {
            throw new BusinessException("手机号已被其他会员使用");
        }

        BeanUtils.copyProperties(dto, member);
        if (StringUtils.hasText(dto.getPassword())) {
            member.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        }
        updateById(member);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        removeById(id);
    }

    @Override
    public MemberVO getMemberById(Long id) {
        Member member = getById(id);
        if (member == null) {
            return null;
        }
        return convertToVO(member);
    }

    @Override
    public MemberVO getMemberByPhone(String phone) {
        Member member = getOne(new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone));
        if (member == null) {
            return null;
        }
        return convertToVO(member);
    }

    @Override
    public Page<MemberVO> getMemberPage(Integer page, Integer size, String name, String phone, String level) {
        Page<Member> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like(Member::getName, name);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(Member::getPhone, phone);
        }
        if (StringUtils.hasText(level)) {
            wrapper.eq(Member::getLevel, level);
        }

        wrapper.orderByDesc(Member::getCreateTime);
        Page<Member> memberPage = page(pageParam, wrapper);

        List<MemberVO> voList = memberPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<MemberVO> voPage = new Page<>();
        BeanUtils.copyProperties(memberPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<MemberVO> getMemberList() {
        List<Member> list = list(new LambdaQueryWrapper<Member>()
                .eq(Member::getStatus, 1)
                .orderByDesc(Member::getCreateTime));
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void rechargeBalance(Long memberId, BigDecimal amount, String source, Long sourceId, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        member.setBalance(member.getBalance().add(amount));
        updateById(member);

        BalanceRecord record = new BalanceRecord();
        record.setMemberId(memberId);
        record.setType("RECHARGE");
        record.setAmount(amount);
        record.setBalance(member.getBalance());
        record.setSource(source);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        balanceRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void consumeBalance(Long memberId, BigDecimal amount, String source, Long sourceId, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        if (member.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        member.setBalance(member.getBalance().subtract(amount));
        updateById(member);

        BalanceRecord record = new BalanceRecord();
        record.setMemberId(memberId);
        record.setType("CONSUME");
        record.setAmount(amount.negate());
        record.setBalance(member.getBalance());
        record.setSource(source);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        balanceRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void addPoints(Long memberId, Integer points, String source, Long sourceId, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        member.setPoints(member.getPoints() + points);
        updateById(member);

        PointsRecord record = new PointsRecord();
        record.setMemberId(memberId);
        record.setType("EARN");
        record.setPoints(points);
        record.setBalance(member.getPoints());
        record.setSource(source);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        pointsRecordMapper.insert(record);

        checkAndUpgradeLevel(member);
    }

    @Override
    @Transactional
    public void deductPoints(Long memberId, Integer points, String source, Long sourceId, String remark) {
        Member member = getById(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }

        if (member.getPoints() < points) {
            throw new BusinessException("积分不足");
        }

        member.setPoints(member.getPoints() - points);
        updateById(member);

        PointsRecord record = new PointsRecord();
        record.setMemberId(memberId);
        record.setType("DEDUCT");
        record.setPoints(-points);
        record.setBalance(member.getPoints());
        record.setSource(source);
        record.setSourceId(sourceId);
        record.setRemark(remark);
        pointsRecordMapper.insert(record);
    }

    @Override
    public Page<PointsRecordVO> getPointsRecordPage(Long memberId, Integer page, Integer size) {
        Page<PointsRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getMemberId, memberId);
        wrapper.orderByDesc(PointsRecord::getCreateTime);

        Page<PointsRecord> recordPage = pointsRecordMapper.selectPage(pageParam, wrapper);

        List<PointsRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertToPointsRecordVO)
                .collect(Collectors.toList());

        Page<PointsRecordVO> voPage = new Page<>();
        BeanUtils.copyProperties(recordPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<BalanceRecordVO> getBalanceRecordPage(Long memberId, Integer page, Integer size) {
        Page<BalanceRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BalanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BalanceRecord::getMemberId, memberId);
        wrapper.orderByDesc(BalanceRecord::getCreateTime);

        Page<BalanceRecord> recordPage = balanceRecordMapper.selectPage(pageParam, wrapper);

        List<BalanceRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertToBalanceRecordVO)
                .collect(Collectors.toList());

        Page<BalanceRecordVO> voPage = new Page<>();
        BeanUtils.copyProperties(recordPage, voPage);
        voPage.setRecords(voList);
        return voPage;
    }

    private void checkAndUpgradeLevel(Member member) {
        List<MemberLevelConfig> configs = memberLevelConfigMapper.selectList(
                new LambdaQueryWrapper<MemberLevelConfig>()
                        .orderByDesc(MemberLevelConfig::getMinPoints));

        for (MemberLevelConfig config : configs) {
            if (member.getPoints() >= config.getMinPoints() ||
                    member.getTotalConsumption().compareTo(config.getMinConsumption()) >= 0) {
                if (!config.getLevelCode().equals(member.getLevel())) {
                    member.setLevel(config.getLevelCode());
                    updateById(member);
                }
                break;
            }
        }
    }

    private String generateMemberNo() {
        return "M" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private MemberVO convertToVO(Member member) {
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(member, vo);
        vo.setLevelName(LEVEL_MAP.getOrDefault(member.getLevel(), member.getLevel()));
        vo.setGenderName(GENDER_MAP.getOrDefault(member.getGender(), "未知"));
        vo.setStatusName(member.getStatus() == 1 ? "正常" : "禁用");
        return vo;
    }

    private PointsRecordVO convertToPointsRecordVO(PointsRecord record) {
        PointsRecordVO vo = new PointsRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setTypeName(POINTS_TYPE_MAP.getOrDefault(record.getType(), record.getType()));
        return vo;
    }

    private BalanceRecordVO convertToBalanceRecordVO(BalanceRecord record) {
        BalanceRecordVO vo = new BalanceRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setTypeName(BALANCE_TYPE_MAP.getOrDefault(record.getType(), record.getType()));
        return vo;
    }
}

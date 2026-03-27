package com.supermarket.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.member.entity.MemberLevel;
import com.supermarket.member.mapper.MemberLevelMapper;
import com.supermarket.member.service.MemberLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    @Override
    public MemberLevel getByLevelCode(Integer levelCode) {
        return baseMapper.selectByLevelCode(levelCode);
    }

    @Override
    public List<MemberLevel> listActiveLevels() {
        QueryWrapper<MemberLevel> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("level_code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public MemberLevel calculateLevelByPoints(Integer points) {
        QueryWrapper<MemberLevel> wrapper = new QueryWrapper<>();
        wrapper.le("min_points", points);
        wrapper.ge("max_points", points);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("level_code");
        wrapper.last("LIMIT 1");
        return baseMapper.selectOne(wrapper);
    }
}

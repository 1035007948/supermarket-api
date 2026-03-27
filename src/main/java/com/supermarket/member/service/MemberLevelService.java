package com.supermarket.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.entity.MemberLevel;

import java.util.List;

public interface MemberLevelService extends IService<MemberLevel> {

    MemberLevel getByLevelCode(Integer levelCode);

    List<MemberLevel> listActiveLevels();

    MemberLevel calculateLevelByPoints(Integer points);
}

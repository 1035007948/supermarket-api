package com.supermarket.member.service;

import com.supermarket.member.dto.PointsOperateDTO;
import com.supermarket.member.entity.PointsRecord;

import java.util.List;

public interface PointsService {

    void earnPoints(Long memberId, Integer points, String source, String orderNo, String description);

    void deductPoints(Long memberId, Integer points, String source, String orderNo, String description);

    PointsRecord operatePoints(PointsOperateDTO operateDTO);

    List<PointsRecord> getMemberPointsRecords(Long memberId);

    Integer calculatePointsByAmount(java.math.BigDecimal amount);
}

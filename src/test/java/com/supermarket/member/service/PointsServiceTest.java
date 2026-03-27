package com.supermarket.member.service;

import com.supermarket.member.dto.PointsOperateDTO;
import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.PointsRecord;
import com.supermarket.member.enums.PointsTypeEnum;
import com.supermarket.member.mapper.MemberMapper;
import com.supermarket.member.mapper.PointsRecordMapper;
import com.supermarket.member.service.impl.PointsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointsServiceTest {

    @Mock
    private PointsRecordMapper pointsRecordMapper;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private PointsServiceImpl pointsService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setPoints(100);
        member.setTotalPoints(200);
    }

    @Test
    void testEarnPoints() {
        when(memberMapper.selectById(1L)).thenReturn(member);
        when(pointsRecordMapper.insert(any(PointsRecord.class))).thenReturn(1);
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        assertDoesNotThrow(() -> pointsService.earnPoints(1L, 50, "消费获得", "ORDER001", "测试获得积分"));

        assertEquals(150, member.getPoints());
        assertEquals(250, member.getTotalPoints());
        verify(pointsRecordMapper, times(1)).insert(any(PointsRecord.class));
        verify(memberMapper, times(1)).updateById(any(Member.class));
    }

    @Test
    void testEarnPoints_MemberNotFound() {
        when(memberMapper.selectById(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointsService.earnPoints(1L, 50, "消费获得", "ORDER001", "测试");
        });

        assertEquals("会员不存在", exception.getMessage());
    }

    @Test
    void testDeductPoints() {
        when(memberMapper.selectById(1L)).thenReturn(member);
        when(pointsRecordMapper.insert(any(PointsRecord.class))).thenReturn(1);
        when(memberMapper.updateById(any(Member.class))).thenReturn(1);

        assertDoesNotThrow(() -> pointsService.deductPoints(1L, 30, "消费抵扣", "ORDER001", "测试抵扣积分"));

        assertEquals(70, member.getPoints());
        verify(pointsRecordMapper, times(1)).insert(any(PointsRecord.class));
    }

    @Test
    void testDeductPoints_InsufficientPoints() {
        member.setPoints(20);
        when(memberMapper.selectById(1L)).thenReturn(member);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pointsService.deductPoints(1L, 30, "消费抵扣", "ORDER001", "测试");
        });

        assertEquals("积分不足", exception.getMessage());
    }

    @Test
    void testGetMemberPointsRecords() {
        PointsRecord record1 = new PointsRecord();
        record1.setId(1L);
        record1.setMemberId(1L);
        record1.setPoints(50);
        record1.setType(PointsTypeEnum.EARN.getCode());

        PointsRecord record2 = new PointsRecord();
        record2.setId(2L);
        record2.setMemberId(1L);
        record2.setPoints(-20);
        record2.setType(PointsTypeEnum.DEDUCT.getCode());

        when(pointsRecordMapper.selectByMemberId(1L)).thenReturn(Arrays.asList(record1, record2));

        List<PointsRecord> result = pointsService.getMemberPointsRecords(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(50, result.get(0).getPoints());
        assertEquals(-20, result.get(1).getPoints());
    }

    @Test
    void testCalculatePointsByAmount() {
        BigDecimal amount = new BigDecimal("100.00");
        Integer points = pointsService.calculatePointsByAmount(amount);

        assertEquals(10, points);
    }

    @Test
    void testCalculatePointsByAmount_Zero() {
        BigDecimal amount = new BigDecimal("5.00");
        Integer points = pointsService.calculatePointsByAmount(amount);

        assertEquals(0, points);
    }
}

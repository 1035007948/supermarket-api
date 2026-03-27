package com.supermarket.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.member.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    @Select("SELECT * FROM points_record WHERE member_id = #{memberId} AND deleted = 0 ORDER BY create_time DESC")
    List<PointsRecord> selectByMemberId(@Param("memberId") Long memberId);
}

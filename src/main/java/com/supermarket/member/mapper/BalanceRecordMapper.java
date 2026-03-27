package com.supermarket.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.member.entity.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BalanceRecordMapper extends BaseMapper<BalanceRecord> {

    @Select("SELECT * FROM balance_record WHERE member_id = #{memberId} AND deleted = 0 ORDER BY create_time DESC")
    List<BalanceRecord> selectByMemberId(@Param("memberId") Long memberId);
}

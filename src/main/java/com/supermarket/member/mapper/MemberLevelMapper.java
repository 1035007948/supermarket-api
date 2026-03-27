package com.supermarket.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.member.entity.MemberLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberLevelMapper extends BaseMapper<MemberLevel> {

    @Select("SELECT * FROM member_level WHERE level_code = #{levelCode} AND deleted = 0")
    MemberLevel selectByLevelCode(@Param("levelCode") Integer levelCode);
}

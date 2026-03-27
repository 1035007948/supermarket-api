package com.supermarket.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.member.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    @Select("SELECT * FROM member WHERE member_no = #{memberNo} AND deleted = 0")
    Member selectByMemberNo(@Param("memberNo") String memberNo);

    @Select("SELECT * FROM member WHERE phone = #{phone} AND deleted = 0")
    Member selectByPhone(@Param("phone") String phone);

    @Select("SELECT COUNT(*) FROM member WHERE deleted = 0")
    Long selectCount();
}

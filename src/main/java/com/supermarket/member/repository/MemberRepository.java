package com.supermarket.member.repository;

import com.supermarket.member.entity.Member;
import com.supermarket.member.entity.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberNo(String memberNo);

    Optional<Member> findByPhone(String phone);

    boolean existsByMemberNo(String memberNo);

    boolean existsByPhone(String phone);

    List<Member> findByLevel(MemberLevel level);
}

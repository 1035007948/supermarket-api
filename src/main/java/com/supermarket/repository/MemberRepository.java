package com.supermarket.repository;

import com.supermarket.entity.Member;
import com.supermarket.enums.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByPhone(String phone);

    List<Member> findByLevel(MemberLevel level);

    List<Member> findByIsActive(Boolean isActive);

    List<Member> findByNameContaining(String name);
}
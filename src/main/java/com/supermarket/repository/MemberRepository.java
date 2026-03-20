package com.supermarket.repository;

import com.supermarket.entity.Member;
import com.supermarket.entity.MemberLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    Page<Member> findByLevel(MemberLevel level, Pageable pageable);
}

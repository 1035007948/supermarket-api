package com.supermarket.api.repository;

import com.supermarket.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 会员数据访问接口
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {

    /**
     * 根据手机号查询会员
     */
    Optional<Member> findByPhone(String phone);

    /**
     * 根据会员卡号查询会员
     */
    Optional<Member> findByCardNumber(String cardNumber);

    /**
     * 判断手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 判断会员卡号是否存在
     */
    boolean existsByCardNumber(String cardNumber);
}

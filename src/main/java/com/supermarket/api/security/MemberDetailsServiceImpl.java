package com.supermarket.api.security;

import com.supermarket.api.entity.Member;
import com.supermarket.api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 会员用户详情服务实现
 */
@Service
public class MemberDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("会员不存在，手机号: " + phone));

        if (member.getStatus() != 1) {
            throw new UsernameNotFoundException("会员账号已被禁用");
        }

        // 这里简化处理，所有会员都有ROLE_MEMBER权限
        return new User(
                member.getPhone(),
                member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"))
        );
    }
}

package com.supermarket.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.member.entity.User;

public interface UserService extends IService<User> {

    User getByUsername(String username);

    boolean validatePassword(String rawPassword, String encodedPassword);
}

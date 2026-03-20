package com.supermarket.service;

import com.supermarket.dto.LoginDTO;
import com.supermarket.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);
}

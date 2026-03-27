package com.supermarket.service;

import com.supermarket.dto.AuthResponse;
import com.supermarket.dto.LoginRequest;
import com.supermarket.dto.RegisterRequest;
import com.supermarket.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("用户注册-成功")
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
    }

    @Test
    @DisplayName("用户注册-用户名已存在")
    void register_DuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateuser");
        request.setPassword("password123");
        authService.register(request);

        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    @DisplayName("用户登录-成功")
    void login_Success() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("loginuser");
        registerRequest.setPassword("password123");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("loginuser", response.getUsername());
    }

    @Test
    @DisplayName("用户登录-用户不存在")
    void login_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    @DisplayName("用户登录-密码错误")
    void login_WrongPassword() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("wrongpassuser");
        registerRequest.setPassword("password123");
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrongpassuser");
        loginRequest.setPassword("wrongpassword");

        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
    }
}

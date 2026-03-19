package com.supermarket.controller;

import com.supermarket.common.Result;
import com.supermarket.entity.User;
import com.supermarket.repository.UserRepository;
import com.supermarket.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(user);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("username", user.getUsername());
            result.put("name", user.getName());
            result.put("role", user.getRole());

            return Result.success(result);
        } catch (BadCredentialsException e) {
            return Result.error(401, "用户名或密码错误");
        } catch (Exception e) {
            return Result.error(500, "登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return Result.error(400, "用户名已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        if (user.getRole() == null) {
            user.setRole("ADMIN");
        }

        User savedUser = userRepository.save(user);
        savedUser.setPassword(null);
        return Result.success(savedUser);
    }

    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error(401, "未登录");
        }

        User user = (User) authentication.getPrincipal();
        user.setPassword(null);
        return Result.success(user);
    }
}
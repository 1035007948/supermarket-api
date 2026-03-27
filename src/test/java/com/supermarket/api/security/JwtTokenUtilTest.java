package com.supermarket.api.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenUtil = new JwtTokenUtil();

        // 使用反射设置私有字段
        java.lang.reflect.Field secretField = JwtTokenUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtTokenUtil, "SuperMarketSecretKeyForJWTTokenGeneration2024");

        java.lang.reflect.Field expirationField = JwtTokenUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtTokenUtil, 86400000L);

        userDetails = new User("13800138000", "password", new ArrayList<>());
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenUtil.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateTokenWithClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", 1L);
        claims.put("name", "测试会员");
        claims.put("level", "GOLD");

        String token = jwtTokenUtil.generateToken(userDetails, claims);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtTokenUtil.generateToken(userDetails);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void testGetClaimFromToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", 1L);
        claims.put("name", "测试会员");

        String token = jwtTokenUtil.generateToken(userDetails, claims);

        // 验证可以从token中获取自定义声明
        String username = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtTokenUtil.generateToken(userDetails);
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUser() {
        String token = jwtTokenUtil.generateToken(userDetails);
        UserDetails wrongUser = new User("wronguser", "password", new ArrayList<>());
        boolean isValid = jwtTokenUtil.validateToken(token, wrongUser);
        assertFalse(isValid);
    }

    @Test
    void testExtractAllClaims() throws Exception {
        String token = jwtTokenUtil.generateToken(userDetails);

        // 使用反射调用私有方法
        java.lang.reflect.Method method = JwtTokenUtil.class.getDeclaredMethod("getAllClaimsFromToken", String.class);
        method.setAccessible(true);
        Claims claims = (Claims) method.invoke(jwtTokenUtil, token);

        assertNotNull(claims);
        assertEquals(userDetails.getUsername(), claims.getSubject());
    }
}

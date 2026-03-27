package com.supermarket.member.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        tokenProvider.init();
    }

    @Test
    void testGenerateToken() {
        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "admin", "password", "ADMIN",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String token = tokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testGetUserIdFromToken() {
        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "admin", "password", "ADMIN",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String token = tokenProvider.generateToken(authentication);
        Long userId = tokenProvider.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void testGetUsernameFromToken() {
        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "admin", "password", "ADMIN",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String token = tokenProvider.generateToken(authentication);
        String username = tokenProvider.getUsernameFromToken(token);

        assertEquals("admin", username);
    }

    @Test
    void testValidateToken_Valid() {
        UserPrincipal userPrincipal = new UserPrincipal(
                1L, "admin", "password", "ADMIN",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String token = tokenProvider.generateToken(authentication);
        boolean isValid = tokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_Invalid() {
        boolean isValid = tokenProvider.validateToken("invalid.token.here");

        assertFalse(isValid);
    }
}

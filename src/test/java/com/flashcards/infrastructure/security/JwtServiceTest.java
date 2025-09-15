package com.flashcards.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void testJwtService_GenerateAndValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void testJwtService_ExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(5);

        Assertions.assertThrows(ExpiredJwtException.class,
            () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testJwtService_InvalidToken() {
        String token = jwtService.generateToken(userDetails);

        UserDetails anotherUser = new User("otheruser", "password", Collections.emptyList());
        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }
}

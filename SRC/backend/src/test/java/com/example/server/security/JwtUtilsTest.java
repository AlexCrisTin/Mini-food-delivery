package com.example.server.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "ThisIsAVeryLongAndVerySecretJWTkeyForConChoCaoBangBoPeCe");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String username = "testuser@example.com";
        String token = jwtUtils.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(username, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void shouldFailForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtils.validateToken(invalidToken));
    }
}

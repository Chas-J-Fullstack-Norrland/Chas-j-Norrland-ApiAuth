package com.example.demo;

import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void shouldGenerateTokenAndContainUsername() {
        String username = "testuser";

        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void shouldValidateCorrectToken() {
        String token = jwtUtil.generateToken("alice");

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void shouldInvalidateTamperedToken() {
        String token = jwtUtil.generateToken("alice");

        // Tamper the token (invalid signature)
        String badToken = token + "abc";

        boolean isValid = jwtUtil.validateToken(badToken);

        assertFalse(isValid);
    }

    @Test
    void shouldExtractCorrectUsername() {
        String username = "bob";

        String token = jwtUtil.generateToken(username);

        String extracted = jwtUtil.extractUsername(token);

        assertEquals(username, extracted);
    }

    @Test
    void shouldFailValidationForMalformedToken() {
        String malformedToken = "this.is.not.valid";

        boolean isValid = jwtUtil.validateToken(malformedToken);

        assertFalse(isValid);
    }
}
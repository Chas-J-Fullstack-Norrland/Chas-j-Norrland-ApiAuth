package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuth2SuccessHandlerTest {

    private JwtUtil jwtUtil;
    private OAuth2SuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        successHandler = new OAuth2SuccessHandler(jwtUtil);
    }

    @Test
    void shouldGenerateJwtAndWriteItToResponse() throws Exception {

        // --- mocks ---
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        // fake user
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("login")).thenReturn("github-user");
        when(oAuth2User.getName()).thenReturn("fallback-user");

        // jwt mock
        when(jwtUtil.generateToken("github-user")).thenReturn("mock-jwt-token");

        // capture response output
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        // --- execute ---
        successHandler.onAuthenticationSuccess(
                request,
                response,
                authentication
        );

        writer.flush();

        // --- verify ---
        verify(jwtUtil, times(1)).generateToken("github-user");

        String output = stringWriter.toString();

        assertTrue(output.contains("mock-jwt-token"));
        assertTrue(output.contains("token"));
    }

}
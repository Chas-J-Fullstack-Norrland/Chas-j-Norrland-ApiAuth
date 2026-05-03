package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
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
    void shouldGenerateJwtAndRedirectWithToken() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("login")).thenReturn("github-user");

        when(jwtUtil.generateToken("github-user")).thenReturn("mock-jwt-token");

        successHandler.onAuthenticationSuccess(
                request,
                response,
                authentication
        );

        verify(jwtUtil, times(1)).generateToken("github-user");

        String redirectUrl = response.getRedirectedUrl();

        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.contains("mock-jwt-token"));
        assertTrue(redirectUrl.contains("token="));
    }

}
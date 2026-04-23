package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import java.util.Collections;


public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

      // Read the Authorization header (expected format: "Bearer <token>")
     String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      // Only handle JWT if header exists and starts with "Bearer "
     if(authHeader != null && authHeader.startsWith("Bearer")){
         // Remove "Bearer " prefix to get raw JWT token
         String token = authHeader.substring(7);

         // ValidateToken returns String ("true"/"false"), convert to boolean
         String validationResult = jwtUtil.validateToken(token);
         boolean isValid = Boolean.parseBoolean(validationResult);

      // Authenticate only when token is valid and no auth is already set
     if(isValid && SecurityContextHolder.getContext().getAuthentication() == null){
         // Extract user identity (subject) from token
         String username = jwtUtil.extractUsername(token);

         // Build authentication object; authorities are empty for now
         UsernamePasswordAuthenticationToken authentication =
                 new UsernamePasswordAuthenticationToken(
                     username,
                     null,
                         Collections.emptyList()
                 );
         //Attach request metadata (IP, session id, etc.)
         authentication.setDetails(
                 new WebAuthenticationDetailsSource().buildDetails(request)
         );
         // Store auth in SecurityContext so Spring treats request as authenticated
         SecurityContextHolder.getContext().setAuthentication(authentication);
        }
     }
        // Continue to next filter (required for request to reach controller)
        chain.doFilter(request, response);
    }
}

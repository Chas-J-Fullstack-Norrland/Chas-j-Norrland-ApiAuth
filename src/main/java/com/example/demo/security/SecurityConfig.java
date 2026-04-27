
package com.example.demo.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuth2SuccessHandler oAuthHandler;

    public SecurityConfig(JwtFilter jwtFilter,OAuth2SuccessHandler oAuthHandler) {
        this.oAuthHandler = oAuthHandler;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public","/register" , "/login").permitAll()
                .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuthHandler)
                )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            // TODO:
            // Lägg till oauth2Login()


        return http.build();
    }
}

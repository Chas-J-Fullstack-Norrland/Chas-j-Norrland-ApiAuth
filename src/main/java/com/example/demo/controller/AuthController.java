
package com.example.demo.controller;
import com.example.demo.model.LoginRequestDTO;
import com.example.demo.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO requestDTO) {
        //1. Försök autentisera via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );
        //2. Om vi kommer hit = Login OK
        String username = authentication.getName();
        //3. Skapa JWT
        String token = jwtUtil.generateToken(username);
        //4. Returnera token
        return token;

    }
}

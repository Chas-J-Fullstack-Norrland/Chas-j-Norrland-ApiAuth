
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

public class JwtUtil {


    // TODO: Implementera:
    // - generateToken(username) ☑
    // - validateToken(token) ☑
    // - extractUsername(token)  ☑


    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    public String generateToken(String username){
        try{
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(
                            System.currentTimeMillis()+1800000
                    ))
                    .signWith(SignatureAlgorithm.HS256,SECRET.getBytes())
                    .compact();
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }

    }

    public String validateToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }





}

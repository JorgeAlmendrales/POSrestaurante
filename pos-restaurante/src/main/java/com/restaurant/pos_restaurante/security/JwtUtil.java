package com.restaurant.pos_restaurante.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET = "pos-restaurante-secret-key-2024-muy-segura-larga";
    private static final long EXPIRATION =
    1000L * 60 * 60 * 24 * 30; // 30 días

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email, String rol, String restauranteId) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .claim("restauranteId", restauranteId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRol(String token) {
        return getClaims(token).get("rol", String.class);
    }

    public String extractRestauranteId(String token) {
        return getClaims(token).get("restauranteId", String.class);
    }

    public boolean isTokenValid(String token) {
    try {
        getClaims(token);
        return true;
    } catch (JwtException | IllegalArgumentException e) {

        System.out.println("=================================");
        System.out.println("ERROR VALIDANDO TOKEN");
        e.printStackTrace();
        System.out.println("=================================");

        return false;
    }
}

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

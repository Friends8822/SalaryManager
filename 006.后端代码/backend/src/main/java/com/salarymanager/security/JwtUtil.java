package com.salarymanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "salary-manager-spring-boot-jwt-secret-key-2026-hmac-sha256";
    private static final long EXPIRATION_MS = 86400000L; // 24h
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
    }

    public static Long getUserId(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public static boolean validateToken(String token) {
        try { parseToken(token); return true; } catch (Exception e) { return false; }
    }
}

package com.example.blog_board.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.blog_board.security.details.PrincipalDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
    private final String SECRET_KEY;                // 32바이트 이상
    private final long ACCESS_TOKEN_EXPIRATION;     // 인증 토큰 유효 시간
    private final long REFRESH_TOKEN_EXPIRATION;    // 갱신 토큰 유효 시간
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String SECRET_KEY,
        @Value("${jwt.accessTokenExpiration}") long ACCESS_TOKEN_EXPIRATION,
        @Value("${jwt.refreshTokenExpiration}") long REFRESH_TOKEN_EXPIRATION) {
        this.SECRET_KEY = SECRET_KEY;
        this.ACCESS_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION;
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(PrincipalDetails principalDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
            .setSubject(principalDetails.getEmail())
            .claim("id", principalDetails.getId())
            .claim("role", principalDetails.getRole())
            .setIssuedAt(now)                           // 토큰 발급 시간(iat)
            .setExpiration(expiryDate)                  // 만료 시간(exp)
            .setId(UUID.randomUUID().toString())        // 고유 식별자(jti)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(PrincipalDetails principalDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
            .setSubject(principalDetails.getEmail())
            .claim("id", principalDetails.getId())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .setId(UUID.randomUUID().toString())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims getBody(String token) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String extractUsername(String token) {
        return this.getBody(token)
            .getSubject();
    }

    public Long extractId(String token) {
        return this.getBody(token)
            .get("id", Long.class);
    }

    public String extractRole(String token) {
        return this.getBody(token)
            .get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}

package com.example.blog_board.security.jwt;

import java.nio.charset.StandardCharsets;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
    private final String SECRET_KEY;                // 32바이트 이상
    private final long ACCESS_TOKEN_EXPIRATION;     // 인증 토큰 유효 시간
    @Getter
    private final long REFRESH_TOKEN_EXPIRATION;    // 갱신 토큰 유효 시간
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String SECRET_KEY,
        @Value("${jwt.accessTokenExpiration}") long ACCESS_TOKEN_EXPIRATION,
        @Value("${jwt.refreshTokenExpiration}") long REFRESH_TOKEN_EXPIRATION) {
        this.SECRET_KEY = SECRET_KEY;
        this.ACCESS_TOKEN_EXPIRATION = ACCESS_TOKEN_EXPIRATION;
        this.REFRESH_TOKEN_EXPIRATION = REFRESH_TOKEN_EXPIRATION;
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));;
    }

    /**
     * 인증 토큰 생성
     * @param principalDetails
     * @return
     */
    public String generateAccessToken(PrincipalDetails principalDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        return Jwts.builder()
            .setSubject(principalDetails.getEmail())
            .claim("id", principalDetails.getId())
            .claim("role", principalDetails.getRole())
            .claim("tokenType", "access")
            .setIssuedAt(now)                           // 토큰 발급 시간(iat)
            .setExpiration(expiryDate)                  // 만료 시간(exp)
            .setId(UUID.randomUUID().toString())        // 고유 식별자(jti)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 리프레시 토큰 생성
     * @param principalDetails
     * @return
     */
    public String generateRefreshToken(PrincipalDetails principalDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
            .setSubject(principalDetails.getEmail())
            .claim("id", principalDetails.getId())
            .claim("tokenType", "refresh")
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .setId(UUID.randomUUID().toString())
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 토큰 파싱
     * @param token
     * @return
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(key)  // 이미 JwtUtil에 key 필드가 있음
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException e) {
            // 파싱/검증 과정에서 발생한 모든 예외 처리
            log.debug("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public Long getId(Claims claims) {
        return claims.get("id", Long.class);
    }

    public String getRole(Claims claims) {
        return claims.get("role", String.class);
    }

    public boolean isAccessToken(Claims claims) {
        return claims.get("tokenType", String.class)
            .equals("access");
    }

    public boolean isRefreshToken(Claims claims) {
        return claims.get("tokenType", String.class)
            .equals("refresh");
    }

    public long getRemainingExpirationMs(Claims claims) {
        // 만료 시각 (Date)
        Date expirationDate = claims.getExpiration();

        // 현재 시간 (밀리초)
        long now = System.currentTimeMillis();

        // 남은 시간(밀리초) = 만료 시각 - 현재 시각
        return expirationDate.getTime() - now;
    }

}

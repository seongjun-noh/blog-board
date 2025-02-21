package com.example.blog_board.security.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.service.redis.RedisService;
import com.example.blog_board.security.details.PrincipalDetails;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String header = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer로 시작하지 않으면 다음 필터로 이동
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 토큰이 있으면 검증
        String token = header.substring(7);

        // 토큰 검증
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null || !jwtUtil.isAccessToken(claims) || redisService.isBlacklisted(token)) {
            chain.doFilter(request, response);
            return;
        }

        // 토큰에서 정보 추출
        Long id = jwtUtil.getId(claims);
        String roleStr = jwtUtil.getRole(claims);
        UserRole role = UserRole.valueOf(roleStr);
        String username = jwtUtil.getUsername(claims);

        // 인증 객체 생성
        PrincipalDetails principalDetails = PrincipalDetails.builder()
            .id(id)
            .role(role)
            .email(username)
            .build();

        Authentication authentication = new JwtAuthentication(principalDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }
}

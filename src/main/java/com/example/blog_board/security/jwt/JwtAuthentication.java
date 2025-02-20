package com.example.blog_board.security.jwt;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.example.blog_board.security.details.PrincipalDetails;

public class JwtAuthentication implements Authentication {
    private final PrincipalDetails principalDetails;
    private boolean authenticated;

    public JwtAuthentication(PrincipalDetails principalDetails) {
        this.principalDetails = principalDetails;
        this.authenticated = true;
    }

    public Long getId() {
        return this.principalDetails.getId();
    }

    @Override
    public String getName() {
        return this.principalDetails.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.principalDetails.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null; // JWT 기반이므로 credentials(비밀번호 등)은 필요 없음
    }

    @Override
    public Object getDetails() {
        return this.principalDetails;
    }

    @Override
    public Object getPrincipal() {
        return this.principalDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }
}

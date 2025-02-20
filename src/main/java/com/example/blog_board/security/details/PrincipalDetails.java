package com.example.blog_board.security.details;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.blog_board.common.enums.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrincipalDetails implements UserDetails {
	private final Long id;
	private final UserRole role;
	private final String email;

	public PrincipalDetails(Long id, UserRole role, String username) {
		this.id = id;
		this.role = role;
		this.email = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public String getPassword() {
		return "";
	}
}

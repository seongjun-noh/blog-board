package com.example.blog_board.security.jwt;

public record JwtDto (
	String grantType,
	String accessToken,
	String refreshToken
) {

}
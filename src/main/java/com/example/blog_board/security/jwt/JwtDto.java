package com.example.blog_board.security.jwt;

public record JwtDto (
	String accessToken,
	String refreshToken
) {

}
package com.example.blog_board.api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRefreshRequest {
	@NotBlank(message = "refreshToken is required.")
	private String refreshToken;
}

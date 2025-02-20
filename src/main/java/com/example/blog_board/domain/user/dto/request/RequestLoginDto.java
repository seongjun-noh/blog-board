package com.example.blog_board.domain.user.dto.request;

import com.example.blog_board.common.validation.annotation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLoginDto {
	@Email(message = "Invalid email format.")
	@NotBlank(message = "Email is required")
	private String email;

	@ValidPassword
	private String password;
}

package com.example.blog_board.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.domain.user.dto.request.RequestLogoutDto;
import com.example.blog_board.domain.user.dto.request.RequestRefreshTokenDto;
import com.example.blog_board.domain.user.dto.request.RequestLoginDto;
import com.example.blog_board.domain.user.dto.request.RequestRegisterDto;
import com.example.blog_board.domain.user.service.UserService;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.security.jwt.JwtDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public ApiResponse register(@Valid @RequestBody RequestRegisterDto requestBody) {
		userService.register(requestBody);

		return ApiResponse.success("register");
	}

	@PostMapping("/login")
	public ApiResponse<JwtDto> login(@Valid @RequestBody RequestLoginDto request) {
		JwtDto response = userService.login(request);

		return ApiResponse.success(response);
	}

	@PostMapping("/logout")
	public ApiResponse logout(@AuthenticationPrincipal  PrincipalDetails principalDetails,
							  @Valid @RequestBody RequestLogoutDto requestBody,
							  HttpServletRequest request) {
		// 헤더에서 액세스 토큰 추출
		String header = request.getHeader("Authorization");
		String accessToken = header.substring(7);

		// 사용자 정보 추출
		String email = principalDetails.getEmail();

		userService.logout(email, accessToken, requestBody.getRefreshToken());

		return ApiResponse.success("logout");
	}

	@PostMapping("/refresh")
	public ApiResponse<JwtDto> refresh(@Valid @RequestBody RequestRefreshTokenDto request) {
		JwtDto response = userService.refresh(request);

		return ApiResponse.success(response);
	}
}

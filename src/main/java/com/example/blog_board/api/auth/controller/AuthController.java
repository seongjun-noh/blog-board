package com.example.blog_board.api.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.api.auth.dto.request.LogoutRequest;
import com.example.blog_board.api.auth.dto.request.TokenRefreshRequest;
import com.example.blog_board.api.auth.dto.request.LoginRequest;
import com.example.blog_board.api.auth.dto.request.RegisterRequest;
import com.example.blog_board.service.auth.AuthService;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.security.jwt.JwtDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	@GetMapping("/loginTest")
	public ApiResponse<String> home() {
		String message = "Hello, Blog Board!";

		return ApiResponse.success(message);
	}

	@PostMapping("/register")
	public ApiResponse register(@Valid @RequestBody RegisterRequest requestBody) {
		authService.register(requestBody);

		return ApiResponse.success("register");
	}

	@PostMapping("/login")
	public ApiResponse<JwtDto> login(@Valid @RequestBody LoginRequest request) {
		JwtDto response = authService.login(request);

		return ApiResponse.success(response);
	}

	@PostMapping("/logout")
	public ApiResponse logout(@AuthenticationPrincipal  PrincipalDetails principalDetails,
							  @Valid @RequestBody LogoutRequest requestBody,
							  HttpServletRequest request) {
		// 헤더에서 액세스 토큰 추출
		String header = request.getHeader("Authorization");
		String accessToken = header.substring(7);

		// 사용자 정보 추출
		String email = principalDetails.getEmail();

		authService.logout(email, accessToken, requestBody.getRefreshToken());

		return ApiResponse.success("logout");
	}

	@PostMapping("/refresh")
	public ApiResponse<JwtDto> refresh(@Valid @RequestBody TokenRefreshRequest request) {
		JwtDto response = authService.refresh(request);

		return ApiResponse.success(response);
	}
}

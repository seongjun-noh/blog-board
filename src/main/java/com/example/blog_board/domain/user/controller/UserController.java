package com.example.blog_board.domain.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.domain.user.dto.RequestRegisterDto;
import com.example.blog_board.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public ApiResponse register(@Valid @RequestBody RequestRegisterDto request) {

		userService.register(request);

		return ApiResponse.success("register");
	}
}

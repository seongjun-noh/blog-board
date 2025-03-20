package com.example.blog_board.api.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.auth.dto.request.RegisterRequest;
import com.example.blog_board.api.user.dto.request.UserUpdateRequest;
import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.service.user.UserService.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

	@PutMapping()
	public ApiResponse updateUser(@AuthenticationPrincipal PrincipalDetails principalDetails,
								  @RequestPart(name = "userData") UserUpdateRequest userData,
								  @RequestPart(name = "profileImage", required = false)MultipartFile profileImage) {
		Long userId = principalDetails.getId();
		userService.updateUser(userId, userData, profileImage);

		return ApiResponse.success("User updated");
	}
}

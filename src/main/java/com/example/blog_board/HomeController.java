package com.example.blog_board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.common.dto.ApiResponse;

@RestController
public class HomeController {

	@GetMapping("/loginTest")
	public ApiResponse<String> home() {
		String message = "Hello, Blog Board!";

		return ApiResponse.success(message);
	}
}

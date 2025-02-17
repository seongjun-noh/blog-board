package com.example.blog_board.common.dto;

public record ApiResponse<T>(
	boolean success,
	String message,
	T data
) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "Request successful", data);
	}

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data);
	}

	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(false, message, null);
	}
}

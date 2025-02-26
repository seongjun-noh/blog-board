package com.example.blog_board.common.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {
	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(HttpStatus.FORBIDDEN, message, cause);
	}
}

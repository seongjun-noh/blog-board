package com.example.blog_board.common.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundException extends ResponseStatusException {
	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(HttpStatus.FORBIDDEN, message, cause);
	}
}

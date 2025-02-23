package com.example.blog_board.api.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestPostCreateDto {
	@NotBlank(message = "Title is required.")
	private String title;

	@NotBlank(message = "Content is required.")
	private String content;
}

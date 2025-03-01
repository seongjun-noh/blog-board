package com.example.blog_board.api.post.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateRequest {
	@NotBlank(message = "Title is required.")
	@Size(max = 100, message = "Title is too long")
	private String title;

	@NotBlank(message = "Content is required.")
	@Size(max = 1000, message = "Content is too long. Maximum allowed is 10000 characters.")
	private String content;

	private List<String> deletedFileNames;
}

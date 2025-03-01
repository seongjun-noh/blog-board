package com.example.blog_board.api.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {
	@NotBlank(message = "Content is required")
	@Size(max = 1000, message = "Content is too long. Maximum allowed is 1000 characters.")
	private String content;

	private Long parentCommentId;
}

package com.example.blog_board.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {
	@NotBlank(message = "postId is required")
	private String content;

	private Long parentCommentId;
}

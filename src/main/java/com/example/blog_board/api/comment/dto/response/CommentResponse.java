package com.example.blog_board.api.comment.dto.response;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentResponse {
	private Long id;
	private String content;
	private String userName;
	private Long postId;
	private Long parentCommentId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

package com.example.blog_board.api.post.dto.response;

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
public class PostResponse {
	Long id;
	String title;
	String content;
	Long userId;
	String userName;
	Integer viewCount;
	Boolean hasAttachments;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
}

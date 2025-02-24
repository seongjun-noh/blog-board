package com.example.blog_board.api.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.api.comment.dto.CommentCreateRequest;
import com.example.blog_board.api.comment.dto.response.CommentResponse;
import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.service.comment.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/posts/{postId}/comments/create")
	public ApiResponse<CommentResponse> createComment(@AuthenticationPrincipal PrincipalDetails principalDetails,
													  @PathVariable(name = "postId") Long postId,
													  @Valid @RequestBody CommentCreateRequest requestBody) {
		Long userId = principalDetails.getId();
		CommentResponse comment = commentService.createComment(userId, postId, requestBody);

		return ApiResponse.success(comment);
	}

	@GetMapping("/posts/{postId}/comments")
	public ApiResponse<PagedModel<CommentResponse>> getComments(@PathVariable(name = "postId") Long postId,
														  Pageable pageable) {
		Page<CommentResponse> comments = commentService.getPostComments(postId, pageable);

		return ApiResponse.success(new PagedModel<>(comments));
	}
}

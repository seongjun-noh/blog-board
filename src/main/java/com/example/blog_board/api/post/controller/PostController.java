package com.example.blog_board.api.post.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.post.dto.request.PostCreateRequest;
import com.example.blog_board.api.post.dto.response.PostResponse;
import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.service.post.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping(value = "/create", consumes = { "multipart/form-data" })
	public ApiResponse createPost(@AuthenticationPrincipal PrincipalDetails principalDetails,
								  @RequestPart("post") PostCreateRequest postData,
								  @RequestPart(name = "files", required = false) List<MultipartFile> files
		) {
		Long userId = principalDetails.getId();

		postService.createPost(userId, postData, files);

		return ApiResponse.success("Post created successfully.");
	}

	@GetMapping()
	public ApiResponse<PagedModel<PostResponse>> getPosts(Pageable pageable) {
		Page<PostResponse> posts = postService.getPosts(pageable);

		return ApiResponse.success(new PagedModel<>(posts));
	}

	@GetMapping("/{postId}")
	public ApiResponse<PostResponse> getPost(@PathVariable(name = "postId") Long postId) {
		PostResponse post = postService.getPost(postId);

		return ApiResponse.success(post);
	}
}

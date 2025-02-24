package com.example.blog_board.service.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.api.comment.dto.CommentCreateRequest;
import com.example.blog_board.api.comment.dto.response.CommentResponse;
import com.example.blog_board.domain.comment.entity.CommentEntity;
import com.example.blog_board.domain.comment.repository.CommentRepository;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.post.repository.PostRepository;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest requestBody) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found."));

		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("Post not found."));

		CommentEntity parentComment = null;
		Long parentCommentId = requestBody.getParentCommentId();
		if (parentCommentId != null) {
			parentComment = commentRepository.findByIdAndPostId(parentCommentId, postId)
				.orElseThrow(() -> new IllegalArgumentException("Parent comment not found."));
		}

		CommentEntity newComment = CommentEntity.builder()
			.content(requestBody.getContent())
			.user(user)
			.post(post)
			.parentComment(parentComment)
			.build();

		CommentEntity savedComment = commentRepository.save(newComment);

		return CommentResponse.builder()
				.id(savedComment.getId())
				.content(savedComment.getContent())
				.userName(savedComment.getUser().getName())
				.postId(savedComment.getPost().getId())
				.parentCommentId(
					savedComment.getParentComment() != null ?
						savedComment.getParentComment().getId() :
						null
				)
				.createdAt(savedComment.getCreatedAt())
				.updatedAt(savedComment.getUpdatedAt())
				.build();
	}

	public Page<CommentResponse> getPostComments(Long postId, Pageable pageable) {
		return commentRepository.findAllByPostId(postId, pageable).map(comment ->
			CommentResponse.builder()
				.id(comment.getId())
				.content(comment.getContent())
				.userName(comment.getUser().getName())
				.postId(comment.getPost().getId())
				.parentCommentId(
					comment.getParentComment() != null ?
						comment.getParentComment().getId() :
						null
				)
				.createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt())
				.build()
		);
	}
}

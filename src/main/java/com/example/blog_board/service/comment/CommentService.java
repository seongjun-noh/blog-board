package com.example.blog_board.service.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.api.comment.dto.request.CommentCreateRequest;
import com.example.blog_board.api.comment.dto.request.CommentUpdateRequest;
import com.example.blog_board.api.comment.dto.response.CommentResponse;
import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.common.error.exception.ForbiddenException;
import com.example.blog_board.common.error.exception.NotFoundException;
import com.example.blog_board.domain.comment.entity.CommentEntity;
import com.example.blog_board.domain.comment.repository.CommentRepository;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.post.repository.PostRepository;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.service.comment.mapper.CommentMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	private final CommentMapper commentMapper;

	@Transactional
	public CommentResponse createComment(Long userId, Long postId, CommentCreateRequest requestBody) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("User not found."));

		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException("Post not found."));

		CommentEntity parentComment = null;
		Long parentCommentId = requestBody.getParentCommentId();
		if (parentCommentId != null) {
			parentComment = commentRepository.findByIdAndPostId(parentCommentId, postId)
				.orElseThrow(() -> new NotFoundException("Parent comment not found."));
		}

		CommentEntity newComment = CommentEntity.builder()
			.content(requestBody.getContent())
			.user(user)
			.post(post)
			.parentComment(parentComment)
			.build();

		CommentEntity savedComment = commentRepository.save(newComment);

		return commentMapper.toDto(savedComment);
	}

	@Transactional(readOnly = true)
	public Page<CommentResponse> getPostComments(Long postId, Pageable pageable) {
		return commentRepository.findAllByPostId(postId, pageable).map(commentMapper::toDto);
	}

	@Transactional
	public CommentResponse updateComment(Long userId, Long postId, Long commentId, CommentUpdateRequest requestBody) {
		CommentEntity comment = commentRepository.findByIdAndPostIdAndUserId(commentId, postId, userId)
			.orElseThrow(() -> new NotFoundException("Comment not found."));

		comment.updateContent(requestBody.getContent());

		CommentEntity updatedComment = commentRepository.save(comment);

		return commentMapper.toDto(updatedComment);
	}

	@Transactional
	public void deleteComment(Long userId, UserRole role, Long postId, Long commentId) {

		CommentEntity comment = commentRepository.findByIdAndPostId(commentId, postId)
			.orElseThrow(() -> new NotFoundException("Comment not found."));

		if (UserRole.ADMIN != role && comment.getUser().getId() != userId) {
			throw new ForbiddenException("Access denied.");
		}

		commentRepository.delete(comment);
	}
}

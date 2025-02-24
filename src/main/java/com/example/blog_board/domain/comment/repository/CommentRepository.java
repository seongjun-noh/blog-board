package com.example.blog_board.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_board.api.comment.dto.response.CommentResponse;
import com.example.blog_board.domain.comment.entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
	Optional<CommentEntity> findByIdAndPostId(Long parentCommentId, Long postId);

	Page<CommentEntity> findAllByPostId(Long postId, Pageable pageable);
}

package com.example.blog_board.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.comment.entity.CommentEntity;

@Repository
public interface CommentRepository{
	Optional<CommentEntity> findByIdAndPostId(Long id, Long postId);

	Page<CommentEntity> findAllByPostId(Long postId, Pageable pageable);

	CommentEntity save(CommentEntity newComment);
}

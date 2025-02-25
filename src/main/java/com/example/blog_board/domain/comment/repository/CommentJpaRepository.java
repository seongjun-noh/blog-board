package com.example.blog_board.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog_board.domain.comment.entity.CommentEntity;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long>, CommentRepository {

	@Query("SELECT c FROM CommentEntity c WHERE c.id = :id AND c.post.id = :postId AND c.isDeleted = false")
	Optional<CommentEntity> findByIdAndPostId(Long id, Long postId);

	@Query("SELECT c FROM CommentEntity c WHERE c.id = :id AND c.user.id = :userId AND c.isDeleted = false")
	Optional<CommentEntity> findByIdAndUserId(Long id, Long userId);

	@Query("SELECT c FROM CommentEntity c WHERE c.post.id = :postId AND c.isDeleted = false")
	Page<CommentEntity> findAllByPostId(Long postId, Pageable pageable);
}

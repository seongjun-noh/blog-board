package com.example.blog_board.domain.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog_board.domain.post.entity.PostEntity;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long>, PostRepository {

	@Override
	@Query("SELECT p FROM PostEntity p WHERE p.id = :postId AND p.isDeleted = false")
	Optional<PostEntity> findById(Long postId);

	@Override
	@Query("SELECT p FROM PostEntity p WHERE p.id = :postId AND p.user.id = :userId AND p.isDeleted = false")
	Optional<PostEntity> findByIdAndUserId(Long postId, Long userId);

	@Override
	@Query("SELECT p FROM PostEntity p WHERE p.isDeleted = false")
	Page<PostEntity> findAll(Pageable pageable);
}

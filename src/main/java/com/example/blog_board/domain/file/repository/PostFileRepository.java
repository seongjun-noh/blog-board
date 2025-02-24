package com.example.blog_board.domain.file.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.file.entity.PostFileEntity;

@Repository
public interface PostFileRepository extends JpaRepository<PostFileEntity, Long> {
	List<PostFileEntity> findByPostId(Long postId);

	Optional<PostFileEntity> findByPostIdAndStoredFileName(Long postId, String storedFileName);
}

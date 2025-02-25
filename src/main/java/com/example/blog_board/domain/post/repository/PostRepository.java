package com.example.blog_board.domain.post.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.post.entity.PostEntity;

@Repository
public interface PostRepository {
	Optional<PostEntity> findById(Long postId);

	Page<PostEntity> findAll(Pageable pageable);

	PostEntity save(PostEntity post);
}

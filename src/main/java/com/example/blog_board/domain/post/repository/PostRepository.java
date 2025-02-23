package com.example.blog_board.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.post.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}

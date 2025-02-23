package com.example.blog_board.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.file.entity.PostFileEntity;

@Repository
public interface PostFileRepository extends JpaRepository<PostFileEntity, Long> {
}

package com.example.blog_board.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.user.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String username);

	boolean existsByEmail(String email);
}

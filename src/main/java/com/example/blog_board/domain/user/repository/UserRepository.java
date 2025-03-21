package com.example.blog_board.domain.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.blog_board.domain.user.entity.UserEntity;

@Repository
public interface UserRepository {
	Optional<UserEntity> findById(Long id);

	Optional<UserEntity> findByEmail(String email);

	boolean existsByEmail(String email);

	void save(UserEntity newUser);
}

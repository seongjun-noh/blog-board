package com.example.blog_board.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog_board.domain.user.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, UserRepository {
	@Override
	Optional<UserEntity> findById(Long id);

	@Override
	Optional<UserEntity> findByEmail(String email);

	@Override
	boolean existsByEmail(String email);
}

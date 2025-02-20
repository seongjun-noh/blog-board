package com.example.blog_board.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.domain.user.dto.RequestRegisterDto;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void register(RequestRegisterDto request) {
		boolean isExistsEmail = userRepository.existsByEmail(request.getEmail());
		if (isExistsEmail) {
			throw new IllegalArgumentException("Email already exists.");
		}

		if (!request.getPassword().equals(request.getPasswordCheck())) {
			throw new IllegalArgumentException("Password does not match.");
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		UserEntity newUser = UserEntity.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.role(UserRole.USER)
			.build();

		userRepository.save(newUser);
	}
}

package com.example.blog_board.domain.user.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.domain.user.dto.request.RequestLoginDto;
import com.example.blog_board.domain.user.dto.request.RequestRegisterDto;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.security.jwt.JwtDto;
import com.example.blog_board.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final JwtUtil jwtUtil;

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

	@Transactional(readOnly = true)
	public JwtDto login(RequestLoginDto request) {
		UserEntity user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Password does not match.");
		}

		PrincipalDetails principalDetails = PrincipalDetails.builder()
				.id(user.getId())
				.role(user.getRole())
				.email(user.getEmail())
				.build();

		String accessToken = jwtUtil.generateAccessToken(principalDetails);
		String refreshToken = jwtUtil.generateRefreshToken(principalDetails);

		// Todo : RefreshToken 저장
		// Todo : Login Log 저장

		return new JwtDto(accessToken, refreshToken);
	}
}

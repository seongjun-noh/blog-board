package com.example.blog_board.domain.user.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.domain.redis.redis.RedisService;
import com.example.blog_board.domain.user.dto.request.RequestRefreshTokenDto;
import com.example.blog_board.domain.user.dto.request.RequestLoginDto;
import com.example.blog_board.domain.user.dto.request.RequestRegisterDto;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.security.jwt.JwtDto;
import com.example.blog_board.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final RedisService redisService;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void register(RequestRegisterDto request) {
		// 이메일 중복 체크
		boolean isExistsEmail = userRepository.existsByEmail(request.getEmail());
		if (isExistsEmail) {
			throw new IllegalArgumentException("Email already exists.");
		}

		// 비밀번호 확인
		if (!request.getPassword().equals(request.getPasswordCheck())) {
			throw new IllegalArgumentException("Password does not match.");
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 사용자 생성
		UserEntity newUser = UserEntity.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.role(UserRole.USER)
			.build();

		// 사용자 저장
		userRepository.save(newUser);
	}

	@Transactional(readOnly = true)
	public JwtDto login(RequestLoginDto request) {
		// 사용자 조회
		UserEntity user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		// 비밀번호 확인
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Password does not match.");
		}

		// 토큰 생성
		PrincipalDetails principalDetails = PrincipalDetails.builder()
				.id(user.getId())
				.role(user.getRole())
				.email(user.getEmail())
				.build();

		String accessToken = jwtUtil.generateAccessToken(principalDetails);
		String refreshToken = jwtUtil.generateRefreshToken(principalDetails);

		// 리프레쉬 토큰 저장
		redisService.saveRefreshToken(user.getEmail(), refreshToken, jwtUtil.getREFRESH_TOKEN_EXPIRATION());

		// Todo : Login Log 저장

		// 토큰 반환
		return new JwtDto(accessToken, refreshToken);
	}

	@Transactional(readOnly = true)
	public JwtDto refresh(RequestRefreshTokenDto request) {
		String oldRefreshToken = request.getRefreshToken();

		// 리프레쉬 토큰 유효성 검사
		Claims oldRefreshTokenClaims = jwtUtil.parseToken(oldRefreshToken);
		if (oldRefreshTokenClaims == null || !jwtUtil.isRefreshToken(oldRefreshTokenClaims) || redisService.isBlacklisted(oldRefreshToken)) {
			throw new IllegalArgumentException("Invalid refresh token.");
		}

		String email = jwtUtil.getUsername(oldRefreshTokenClaims);

		// 리프레쉬 토큰 검사
		String storedRefreshToken = redisService.getRefreshToken(email);
		if (storedRefreshToken == null || !oldRefreshToken.equals(storedRefreshToken)) {
			throw new IllegalArgumentException("Invalid or expired refresh token.");
		}

		// 사용자 조회
		UserEntity user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		// 토큰 재발급
		PrincipalDetails principalDetails = PrincipalDetails.builder()
			.id(user.getId())
			.role(user.getRole())
			.email(user.getEmail())
			.build();

		String newAccessToken = jwtUtil.generateAccessToken(principalDetails);
		String newRefreshToken = jwtUtil.generateRefreshToken(principalDetails);

		// 리프레쉬 토큰 저장
		redisService.saveRefreshToken(user.getEmail(), newRefreshToken, jwtUtil.getREFRESH_TOKEN_EXPIRATION());

		// 블랙리스트 저장
		redisService.saveBlackList(oldRefreshToken, jwtUtil.getRemainingExpirationMs(oldRefreshTokenClaims));

		// 토큰 반환
		return new JwtDto(newAccessToken, newRefreshToken);
	}
}

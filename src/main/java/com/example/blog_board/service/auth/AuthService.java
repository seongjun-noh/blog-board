package com.example.blog_board.service.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.service.redis.RedisService;
import com.example.blog_board.api.auth.dto.request.RequestLoginDto;
import com.example.blog_board.api.auth.dto.request.RequestRefreshTokenDto;
import com.example.blog_board.api.auth.dto.request.RequestRegisterDto;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.security.details.PrincipalDetails;
import com.example.blog_board.security.jwt.JwtDto;
import com.example.blog_board.security.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final RedisService redisService;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void register(RequestRegisterDto requestBody) {
		// 이메일 중복 체크
		boolean isExistsEmail = userRepository.existsByEmail(requestBody.getEmail());
		if (isExistsEmail) {
			throw new IllegalArgumentException("Email already exists.");
		}

		// 비밀번호 확인
		if (!requestBody.getPassword().equals(requestBody.getPasswordCheck())) {
			throw new IllegalArgumentException("Password does not match.");
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(requestBody.getPassword());

		// 사용자 생성
		UserEntity newUser = UserEntity.builder()
			.email(requestBody.getEmail())
			.password(encodedPassword)
			.name(requestBody.getName())
			.role(UserRole.USER)
			.build();

		// 사용자 저장
		userRepository.save(newUser);
	}

	@Transactional(readOnly = true)
	public JwtDto login(RequestLoginDto requestBody) {
		// 사용자 조회
		UserEntity user = userRepository.findByEmail(requestBody.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("User not found."));

		// 비밀번호 확인
		if (!passwordEncoder.matches(requestBody.getPassword(), user.getPassword())) {
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

	public void logout(String email, String accessToken, String refreshToken) {
		// 액세스 토큰 블랙리스트 저장
		Claims accessTokenClaims = jwtUtil.parseToken(accessToken);
		redisService.saveBlackList(accessToken, jwtUtil.getRemainingExpirationMs(accessTokenClaims));

		// 리프레쉬 토큰 삭제
		redisService.deleteRefreshToken(email);
		// 리프레쉬 토큰 블랙리스트 저장
		Claims refreshTokenClaims = jwtUtil.parseToken(refreshToken);
		redisService.saveBlackList(refreshToken, jwtUtil.getRemainingExpirationMs(refreshTokenClaims));
	}

	@Transactional(readOnly = true)
	public JwtDto refresh(RequestRefreshTokenDto requestBody) {
		String oldRefreshToken = requestBody.getRefreshToken();

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

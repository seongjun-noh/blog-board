package com.example.blog_board.service.user.UserService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.user.dto.request.UserUpdateRequest;
import com.example.blog_board.common.error.exception.NotFoundException;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.service.file.PostFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final FileService fileService;

	public void updateUser(Long userId, UserUpdateRequest userData, MultipartFile profileImage) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException("User not found"));

		user.updateName(userData.getName());

		userRepository.save(user);
	}
}

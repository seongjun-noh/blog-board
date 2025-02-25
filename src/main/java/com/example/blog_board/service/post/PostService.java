package com.example.blog_board.service.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.post.dto.request.PostCreateRequest;
import com.example.blog_board.api.post.dto.response.PostResponse;
import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.common.util.FileUtil;
import com.example.blog_board.domain.file.entity.PostFileEntity;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.post.repository.PostRepository;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.service.file.PostFileService;
import com.example.blog_board.service.post.mapper.PostMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostFileService postFileService;

	private final PostMapper postMapper;

	@Transactional
	public PostResponse createPost(Long userId, PostCreateRequest postData, List<MultipartFile> files) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found."));

		// 게시글 생성
		PostEntity newPost = PostEntity.builder()
			.user(user)
			.title(postData.getTitle())
			.content(postData.getContent())
			.build();

		// 게시글 저장
		PostEntity savedPost = postRepository.save(newPost);

		// 파일 저장
		if (files != null && !files.isEmpty()) {
			List<PostFileEntity> savedFiles = new ArrayList<>();

			try {
				int index = 1;
				for (MultipartFile file : files) {
					if (file.isEmpty()) {
						continue;
					}

					PostFileEntity savedFile = postFileService.saveFile(savedPost, index, file);
					savedFiles.add(savedFile);
					index++;
				}
			} catch (IOException e) {
				// 파일 저장 실패 시
				savedFiles.forEach(file ->
					FileUtil.deleteLocalFile(file.getFilePath())
				);

				log.error("Failed to save files.", e);
				throw new IllegalArgumentException("Failed to save files.");
			}
		}

		return postMapper.toDto(savedPost);
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> getPosts(Pageable pageable) {

		return postRepository.findAll(pageable).map(postMapper::toDto);
	}

	@Transactional
	public PostResponse getPost(Long postId) {
		// 게시글 조회
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalStateException("Post not found."));

		// 조회수 증가
		post.addViewCount();
		PostEntity updatedPost = postRepository.save(post);

		return postMapper.toDto(updatedPost);
	}

	public void deleteUserPost(Long userId, UserRole role, Long postId) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalStateException("Post not found."));

		if (UserRole.ADMIN != role && post.getUser().getId() != userId) {
			throw new IllegalStateException("Access denied.");
		}

		postRepository.delete(post);
	}
}

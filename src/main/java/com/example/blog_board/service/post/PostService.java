package com.example.blog_board.service.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.post.dto.request.PostCreateRequest;
import com.example.blog_board.api.post.dto.request.PostUpdateRequest;
import com.example.blog_board.api.post.dto.response.PostResponse;
import com.example.blog_board.common.enums.UserRole;
import com.example.blog_board.common.error.exception.ForbiddenException;
import com.example.blog_board.common.error.exception.NotFoundException;
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
			.orElseThrow(() -> new NotFoundException("User not found."));

		// 게시글 생성
		PostEntity newPost = PostEntity.builder()
			.user(user)
			.title(postData.getTitle())
			.content(postData.getContent())
			.build();

		// 게시글 저장
		PostEntity savedPost = postRepository.save(newPost);

		// 파일 저장
		postFileService.saveFiles(savedPost, files);

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
			.orElseThrow(() -> new NotFoundException("Post not found."));

		// 조회수 증가
		post.addViewCount();
		PostEntity updatedPost = postRepository.save(post);

		return postMapper.toDto(updatedPost);
	}

	@Transactional
	public PostResponse updatePost(Long userId, Long postId, PostUpdateRequest postData, List<MultipartFile> files) {
		PostEntity post = postRepository.findByIdAndUserId(postId, userId)
			.orElseThrow(() -> new NotFoundException("Post not found."));

		post.updateTitle(postData.getTitle());
		post.updateContent(postData.getContent());

		PostEntity updatedPost = postRepository.save(post);

		// 파일 삭제
		if (postData.getDeletedFileNames() != null && !postData.getDeletedFileNames().isEmpty()) {
			for (String fileName : postData.getDeletedFileNames()) {
				postFileService.deleteFileByPostIdAndFileName(postId, fileName);
			}
		}

		// 파일 저장
		postFileService.saveFiles(updatedPost, files);

		return postMapper.toDto(updatedPost);
	}

	@Transactional
	public void deleteUserPost(Long userId, UserRole role, Long postId) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new NotFoundException("Post not found."));

		if (UserRole.ADMIN != role && post.getUser().getId() != userId) {
			throw new ForbiddenException("Access denied.");
		}

		List<PostFileEntity> files = post.getFiles();
		if (files != null && !files.isEmpty()) {
			for (PostFileEntity file : files) {
				postFileService.deleteFileByPostIdAndFileName(postId, file.getStoredFileName());
			}
		}

		postRepository.delete(post);
	}
}

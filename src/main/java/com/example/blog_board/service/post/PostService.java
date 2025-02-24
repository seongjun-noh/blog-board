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
import com.example.blog_board.common.util.FileUtil;
import com.example.blog_board.domain.file.entity.PostFileEntity;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.post.repository.PostRepository;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.domain.user.repository.UserRepository;
import com.example.blog_board.service.file.PostFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostFileService postFileService;

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

		return PostResponse.builder()
				.id(savedPost.getId())
				.title(savedPost.getTitle())
				.content(savedPost.getContent())
				.userId(savedPost.getUser().getId())
				.userName(savedPost.getUser().getName())
				.viewCount(savedPost.getViewCount())
				.hasAttachments(savedPost.hasFiles())
				.createdAt(savedPost.getCreatedAt())
				.updatedAt(savedPost.getUpdatedAt())
				.build();
	}

	@Transactional(readOnly = true)
	public Page<PostResponse> getPosts(Pageable pageable) {

		return postRepository.findAll(pageable).map(post ->
			PostResponse.builder()
				.id(post.getId())
				.title(post.getTitle())
				.content(post.getTruncateContent(100))
				.userId(post.getUser().getId())
				.userName(post.getUser().getName())
				.viewCount(post.getViewCount())
				.hasAttachments(post.hasFiles())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build()
		);
	}

	@Transactional
	public PostResponse getPost(Long postId) {
		// 게시글 조회
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalStateException("Post not found."));

		// 조회수 증가
		post.addViewCount();
		postRepository.save(post);
		
		return PostResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.userId(post.getUser().getId())
			.userName(post.getUser().getName())
			.viewCount(post.getViewCount())
			.hasAttachments(post.hasFiles())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}
}

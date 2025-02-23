package com.example.blog_board.service.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.post.dto.request.RequestPostCreateDto;
import com.example.blog_board.api.post.dto.response.ResponsePoseDto;
import com.example.blog_board.domain.file.entity.PostFileEntity;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.domain.post.repository.PostRepository;
import com.example.blog_board.domain.user.entity.UserEntity;
import com.example.blog_board.service.file.PostFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PostFileService postFileService;

	@Transactional
	public void createPost(Long userId, RequestPostCreateDto postData, List<MultipartFile> files) {
		// 게시글 생성
		PostEntity newPost = PostEntity.builder()
			.user(UserEntity.builder().id(userId).build())
			.title(postData.getTitle())
			.content(postData.getContent())
			.build();

		// 게시글 저장
		PostEntity savedPost = postRepository.save(newPost);

		// 파일 저장
		List<PostFileEntity> savedFiles = new ArrayList<>();
		try {
			int index = 1;
			for (MultipartFile file : files) {
				PostFileEntity savedFile = postFileService.saveFile(savedPost, index, file);
				savedFiles.add(savedFile);
				index++;
			}
		} catch (IOException e) {
			// 파일 저장 실패 시
			savedFiles.forEach(file ->
				postFileService.deleteFile(file.getFilePath())
			);

			log.error("Failed to save files.", e);
			throw new IllegalArgumentException("Failed to save files.");
		}
	}

	@Transactional(readOnly = true)
	public Page<ResponsePoseDto> getPosts(Pageable pageable) {

		return postRepository.findAll(pageable).map(post ->
			ResponsePoseDto.builder()
				.id(post.getId())
				.title(post.getTitle())
				.content(post.getTruncateContent(100))
				.userId(post.getUser().getId())
				.userName(post.getUser().getName())
				.viewCount(post.getViewCount())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build()
		);
	}

	public ResponsePoseDto getPost(Long postId) {
		PostEntity post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalStateException("Post not found."));

		return ResponsePoseDto.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.userId(post.getUser().getId())
			.userName(post.getUser().getName())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}
}

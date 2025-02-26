package com.example.blog_board.service.file;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.blog_board.api.file.dto.response.FileResponse;
import com.example.blog_board.common.error.exception.BadRequestException;
import com.example.blog_board.common.error.exception.NotFoundException;
import com.example.blog_board.common.util.FileUtil;
import com.example.blog_board.domain.file.entity.PostFileEntity;
import com.example.blog_board.domain.file.repository.PostFileRepository;
import com.example.blog_board.domain.post.entity.PostEntity;
import com.example.blog_board.service.file.mapper.FileMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostFileService {
	private final PostFileRepository postFileRepository;

	private final FileMapper fileMapper;

	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;

	private final int MAX_FILE_SIZE = 50 * 1024 * 1024;

	private String[] blacklistedMimeTypes = {
		"application/x-msdownload", // exe, dll 등 실행 파일
		"application/x-sh",         // 쉘 스크립트
		"application/x-bat",        // 배치 파일
		"application/javascript",   // 스크립트 (특정 상황에서는 위험할 수 있음)
		"text/javascript",          // 스크립트
		"application/x-php",        // PHP 파일
		"application/php",          // PHP 파일
		"text/php",                 // PHP 파일
		"application/x-msdos-program" // DOS 프로그램
	};


	@Transactional
	public PostFileEntity saveFile(PostEntity post, int order, MultipartFile multipartFile) throws IOException {
		// 원본 파일명, 사이즈, MIME 타입 추출
		String originalName = multipartFile.getOriginalFilename();
		long size = multipartFile.getSize();
		String contentType = multipartFile.getContentType();

		// 파일 형식 검증
		if (FileUtil.isValidContentType(contentType, blacklistedMimeTypes)) {
			throw new BadRequestException("Invalid file type.");
		}

		// 파일 크기 검증
		if (size > MAX_FILE_SIZE) {
			throw new BadRequestException("File size exceeds the limit.");
		}

		// 저장 파일명 생성
		String storedFileName = FileUtil.createStoredFileName(originalName);

		// 로컬 디렉토리 경로
		String uploadDir = UPLOAD_DIR;

		// 실제 디스크에 저장
		FileUtil.saveFileToLocal(multipartFile, uploadDir, storedFileName);

		// DB 저장(메타데이터)
		PostFileEntity fileEntity = PostFileEntity.builder()
			.originalFileName(originalName)
			.storedFileName(storedFileName)
			.fileSize(size)
			.fileType(contentType)
			.filePath(UPLOAD_DIR + "/" + storedFileName)
			.post(post)
			.build();

		return postFileRepository.save(fileEntity);
	}

	@Transactional(readOnly = true)
	public List<FileResponse> getFilesByPostId(Long postId) {
		List<PostFileEntity> files = postFileRepository.findByPostId(postId);

		return files.stream().map(fileMapper::toDto).toList();
	}

	@Transactional(readOnly = true)
	public FileResponse getFilesByPostIdAndStoredFileName(Long postId, String storedFileName) {
		PostFileEntity file = postFileRepository.findByPostIdAndStoredFileName(postId, storedFileName)
			.orElseThrow(() -> new NotFoundException("file not found."));

		return fileMapper.toDto(file);
	}
}


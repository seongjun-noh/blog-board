package com.example.blog_board.api.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog_board.api.file.dto.response.FileResponse;
import com.example.blog_board.common.dto.ApiResponse;
import com.example.blog_board.service.file.PostFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {
	private final PostFileService postFileService;

	@GetMapping("/posts/{postId}/files")
	public ApiResponse<List<FileResponse>> getPostAttachments(@PathVariable(name = "postId") Long postId) {
		List<FileResponse> files = postFileService.getFilesByPostId(postId);

		return ApiResponse.success(files);
	}

	@GetMapping("/posts/{postId}/files/{storedFileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable(name = "postId") Long postId,
												 @PathVariable(name = "storedFileName") String storedFileName) throws IOException {
		// 파일 메타데이터를 조회합니다.
		FileResponse fileInfo = postFileService.getFilesByPostIdAndStoredFileName(postId, storedFileName);

		File file = new File(fileInfo.getFilePath());
		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(fileInfo.getFileType()))
			.header(
				HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + fileInfo.getOriginalFileName() + "\""
			)
			.contentLength(file.length())
			.body(resource);
	}
}

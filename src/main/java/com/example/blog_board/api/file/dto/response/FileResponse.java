package com.example.blog_board.api.file.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileResponse {
	private Long id;
	private String originalFileName;
	private String storedFileName;
	private String filePath;
	private String fileType;
	private Long fileSize;
}

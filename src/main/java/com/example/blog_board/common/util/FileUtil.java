package com.example.blog_board.common.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    // 파일 이름 생성 (UUID + 확장자)
    public static String createStoredFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = getFileExtension(originalFilename); // 원본 파일에서 확장자만 추출
        return uuid + ext;
    }

    // 파일 확장자 추출
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex != -1) {
            return filename.substring(dotIndex); // 예: ".png"
        }
        return "";
    }

    // MultipartFile을 지정된 경로로 저장 (로컬 디스크)
    public static void saveFileToLocal(MultipartFile multipartFile, String absoluteDirPath, String storedFileName) throws IOException {
        // 디렉토리가 없으면 생성
        File dir = new File(absoluteDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 실제 저장할 파일 객체
        File dest = new File(absoluteDirPath, storedFileName);
        multipartFile.transferTo(dest);
    }

    public static boolean deleteLocalFile(String absoluteFilePath) {
        File file = new File(absoluteFilePath);
        if (file.exists()) {
            return file.delete(); // 삭제 성공 시 true, 실패 시 false
        }
        return false;
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    // 파일 사이즈 확인 (KB, MB 변환)
    public static String readableFileSize(long size) {
        // 간단 예시
        int unit = 1024;
        if (size < unit) {
            return size + " B";
        }
        int exp = (int) (Math.log(size) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
    }

    // MIME 타입 검증
    public static boolean isValidContentType(String contentType, String... allowedTypes) {
        if (contentType == null) {
            return false;
        }
        for (String allowed : allowedTypes) {
            if (contentType.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllowedExtension(String filename, String... allowedExts) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }

        // 확장자 추출 (예: ".jpg")
        String ext = getFileExtension(filename).toLowerCase();

        // 화이트리스트와 비교
        for (String allowed : allowedExts) {
            // ".jpg" 식으로 정확히 일치해야 함
            if (ext.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }
}

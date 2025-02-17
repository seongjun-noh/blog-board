package com.example.blog_board.common.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.blog_board.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        String message = ex.getMessage();
        log.warn("IllegalStateException: {}", message);

        /** 이미 처리된 요청을 다시 시도하는 경우 **/
        if (message.contains("already published")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(message));
        }

        /** 특정 조건에 따라 리소스가 존재하지 않는 경우 **/
        if (message.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(message));
        }

        /** 현재 사용자가 해당 작업을 수행할 권한이 없는 경우 **/
        if (message.contains("not allowed")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(message));
        }

        /** 특정 조건이 충족되지 않아 요청을 처리할 수 없는 경우 **/
        return ResponseEntity.unprocessableEntity().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Internal Server Error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Internal Server Error"));
    }
}

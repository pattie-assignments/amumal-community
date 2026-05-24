package com.stocat.amumal.user.controller;

import com.stocat.amumal.user.dto.ApiResponse;
import com.stocat.amumal.user.exception.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 전역 예외 처리기
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 의도된 예외 처리 (400, 409 등 ApiException로 직접 던진 예외)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(ApiResponse.of(exception.getMessage(), null));
    }

    // 예상치 못한 예외 처리 (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of("서버 내부 오류가 발생했습니다.", null));
    }
}

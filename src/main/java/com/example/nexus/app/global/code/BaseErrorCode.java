package com.example.nexus.app.global.code;

import org.springframework.http.HttpStatus;
import com.example.nexus.app.global.code.dto.ApiResponse;

public interface BaseErrorCode {
    boolean isSuccess();
    String getCode();
    String getMessage();
    HttpStatus getHttpStatus();

    default <T> ApiResponse<T> toResponse(T data) {
        return ApiResponse.<T>builder()
                .success(isSuccess())
                .code(getCode())
                .message(getMessage())
                .data(data)
                .build();
    }
}

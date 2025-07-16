package com.example.nexus.app.global.code.dto;

import lombok.Builder;
import lombok.Getter;
import com.example.nexus.app.global.code.BaseErrorCode;

@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> of(BaseErrorCode rc, T data) {
        return rc.toResponse(data);
    }
}

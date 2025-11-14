package com.example.nexus.app.feedback.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "스크린샷 업로드용 Presigned URL 요청")
public record PresignedUrlRequest(

        @Schema(description = "파일명 (확장자 포함)", example = "screenshot.png")
        @NotBlank(message = "파일명은 필수입니다.")
        String fileName,

        @Schema(description = "파일 타입", example = "image/png")
        @NotBlank(message = "파일 타입은 필수입니다.")
        @Pattern(regexp = "image/(png|jpg|jpeg|gif|webp)", message = "이미지 파일만 업로드 가능합니다.")
        String contentType
) {
}


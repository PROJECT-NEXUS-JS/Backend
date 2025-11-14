package com.example.nexus.app.feedback.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스크린샷 업로드용 Presigned URL 응답")
public record PresignedUrlResponse(

        @Schema(description = "업로드용 Presigned URL")
        String uploadUrl,

        @Schema(description = "업로드 후 접근할 파일 URL")
        String fileUrl,

        @Schema(description = "만료 시간 (초)", example = "300")
        Long expirationSeconds
) {
}


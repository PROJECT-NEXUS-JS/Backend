package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.message.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;

public record MessageFileInfo(
        @Schema(description = "파일 URL")
        String fileUrl,

        @Schema(description = "원본 파일명")
        String fileName,

        @Schema(description = "파일 크기 (bytes)")
        Long fileSize,

        @Schema(description = "파일 타입")
        String fileType
) {
    public static MessageFileInfo from(Message message) {
        return new MessageFileInfo(
                message.getFileUrl(),
                message.getFileName(),
                message.getFileSize(),
                message.getMessageType().name()
        );
    }
}

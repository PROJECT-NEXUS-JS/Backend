package com.example.nexus.app.message.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "메시지 타입")
public enum MessageType {
    @Schema(description = "텍스트")
    TEXT("텍스트"),

    @Schema(description = "파일")
    FILE("파일"),

    @Schema(description = "이미지")
    IMAGE("이미지");

    private final String description;
}

package com.example.nexus.app.category.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "메인 카테고리")
public enum MainCategory {
    @Schema(description = "웹")
    WEB("웹"),

    @Schema(description = "앱")
    APP("앱"),

    @Schema(description = "게임")
    GAME("게임"),

    @Schema(description = "기타")
    ETC("기타");

    private final String description;
}

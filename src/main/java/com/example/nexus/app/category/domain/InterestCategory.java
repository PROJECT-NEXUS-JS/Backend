package com.example.nexus.app.category.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "관심사 카테고리")
public enum InterestCategory {
    @Schema(description = "앱")
    APP("앱"),

    @Schema(description = "웹")
    WEB("웹"),

    @Schema(description = "게임")
    GAME("게임"),

    @Schema(description = "UX 피드백")
    UX_FEEDBACK("UX 피드백"),

    @Schema(description = "AI")
    AI("AI"),

    @Schema(description = "기능 검증")
    FUNCTION_VALIDATION("기능 검증"),

    @Schema(description = "설문형 테스트")
    SURVEY("설문형 테스트"),

    @Schema(description = "신규 서비스 런칭")
    NEW_SERVICE_LAUNCH("신규 서비스 런칭"),

    @Schema(description = "리워드 있음")
    HAS_REWARD("리워드 있음"),

    @Schema(description = "실시간 테스트 참여")
    REALTIME_TEST("실시간 테스트 참여"),

    @Schema(description = "핀테크")
    FINTECH("핀테크"),

    @Schema(description = "피트니스")
    FITNESS("피트니스"),

    @Schema(description = "소셜/커뮤니티")
    SOCIAL_COMMUNITY("소셜/커뮤니티"),

    @Schema(description = "여행/모빌리티")
    TRAVEL_MOBILITY("여행/모빌리티");

    private final String description;
}

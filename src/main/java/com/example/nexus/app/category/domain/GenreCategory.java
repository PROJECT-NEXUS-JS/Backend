package com.example.nexus.app.category.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "장르 카테고리")
public enum GenreCategory {
    // 앱
    @Schema(description = "라이프스타일")
    LIFESTYLE("라이프스타일"),

    @Schema(description = "교육/학습")
    EDUCATION("교육/학습"),

    @Schema(description = "소셜/커뮤니티")
    SOCIAL("소셜/커뮤니티"),

    @Schema(description = "AI/실험적 기능")
    AI_EXPERIMENTAL("AI/실험적 기능"),

    @Schema(description = "생산성/도구")
    PRODUCTIVITY("생산성/도구"),

    @Schema(description = "커머스/쇼핑")
    COMMERCE("커머스/쇼핑"),

    @Schema(description = "건강/운동")
    HEALTH_FITNESS("건강/운동"),

    @Schema(description = "엔터테인먼트")
    ENTERTAINMENT("엔터테인먼트"),

    @Schema(description = "금융/자산관리")
    FINANCE("금융/자산관리"),

    @Schema(description = "비즈니스/직장인")
    BUSINESS("비즈니스/직장인"),

    @Schema(description = "사진/영상")
    MEDIA("사진/영상"),

    // 웹
    @Schema(description = "생산성/협업툴")
    PRODUCTIVITY_COLLABORATION("생산성/협업툴"),

    @Schema(description = "커머스/쇼핑")
    COMMERCE_SHOPPING_WEB("커머스/쇼핑"),

    @Schema(description = "마케팅/홍보툴")
    MARKETING_PROMOTION("마케팅/홍보툴"),

    @Schema(description = "커뮤니티/소셜")
    COMMUNITY_SOCIAL_WEB("커뮤니티/소셜"),

    @Schema(description = "교육/콘텐츠")
    EDUCATION_CONTENT("교육/콘텐츠"),

    @Schema(description = "금융/자산관리")
    FINANCE_ASSET("금융/자산관리"),

    @Schema(description = "AI/자동화 도구")
    AI_AUTOMATION("AI/자동화 도구"),

    @Schema(description = "실험적 웹툴")
    EXPERIMENTAL_WEB("실험적 웹툴"),

    @Schema(description = "라이프 스타일/취미")
    LIFESTYLE_HOBBY("라이프 스타일/취미"),

    @Schema(description = "채용/HR")
    RECRUITMENT_HR("채용/HR"),

    @Schema(description = "고객관리/세일즈")
    CRM_SALES("고객관리/세일즈"),

    // 게임
    @Schema(description = "캐주얼")
    CASUAL("캐주얼"),

    @Schema(description = "퍼즐/보드")
    PUZZLE_BOARD("퍼즐/보드"),

    @Schema(description = "RPG/어드벤처")
    RPG_ADVENTURE("RPG/어드벤처"),

    @Schema(description = "시뮬레이션")
    SIMULATION_GAME("시뮬레이션"),

    @Schema(description = "전략/카드")
    STRATEGY_CARD("전략/카드"),

    @Schema(description = "스포츠/레이싱")
    SPORTS_RACING("스포츠/레이싱"),

    @Schema(description = "멀티플레이/소셜")
    MULTIPLAYER_SOCIAL("멀티플레이/소셜"),

    @Schema(description = "기타")
    ETC("기타");

    private final String description;
}

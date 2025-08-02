package com.example.nexus.app.category.domain;

public enum GenreCategory {
    // 앱
    LIFESTYLE("라이프스타일", "LIFESTYLE"),
    EDUCATION("교육/학습", "EDUCATION"),
    SOCIAL("소셜/커뮤니티", "SOCIAL"),
    AI_EXPERIMENTAL("AI/실험적 기능", "AI_EXPERIMENTAL"),
    PRODUCTIVITY("생산성/도구", "PRODUCTIVITY"),
    COMMERCE("커머스/쇼핑", "COMMERCE"),
    HEALTH_FITNESS("건강/운동", "HEALTH_FITNESS"),
    ENTERTAINMENT("엔터테인먼트", "ENTERTAINMENT"),
    FINANCE("금융/자산관리", "FINANCE"),
    BUSINESS("비즈니스/직장인", "BUSINESS"),
    MEDIA("사진/영상", "MEDIA"),
    
    // 웹
    PRODUCTIVITY_COLLABORATION("생산성/협업툴", "PRODUCTIVITY_COLLABORATION"),
    COMMERCE_SHOPPING_WEB("커머스/쇼핑", "COMMERCE_SHOPPING_WEB"),
    MARKETING_PROMOTION("마케팅/홍보툴", "MARKETING_PROMOTION"),
    COMMUNITY_SOCIAL_WEB("커뮤니티/소셜", "COMMUNITY_SOCIAL_WEB"),
    EDUCATION_CONTENT("교육/콘텐츠", "EDUCATION_CONTENT"),
    FINANCE_ASSET("금융/자산관리", "FINANCE_ASSET"),
    AI_AUTOMATION("AI/자동화 도구", "AI_AUTOMATION"),
    EXPERIMENTAL_WEB("실험적 웹툴", "EXPERIMENTAL_WEB"),
    LIFESTYLE_HOBBY("라이프 스타일/취미", "LIFESTYLE_HOBBY"),
    RECRUITMENT_HR("채용/HR", "RECRUITMENT_HR"),
    CRM_SALES("고객관리/세일즈", "CRM_SALES"),

    // 게임
    CASUAL("캐주얼", "CASUAL"),
    PUZZLE_BOARD("퍼즐/보드", "PUZZLE_BOARD"),
    RPG_ADVENTURE("RPG/어드벤처", "RPG_ADVENTURE"),
    SIMULATION_GAME("시뮬레이션", "SIMULATION_GAME"),
    STRATEGY_CARD("전략/카드", "STRATEGY_CARD"),
    SPORTS_RACING("스포츠/레이싱", "SPORTS_RACING"),
    MULTIPLAYER_SOCIAL("멀티플레이/소셜", "MULTIPLAYER_SOCIAL"),

    ETC("기타", "ETC");

    private final String displayName;
    private final String code;

    GenreCategory(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}

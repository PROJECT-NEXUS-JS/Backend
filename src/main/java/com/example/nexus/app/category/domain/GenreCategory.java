package com.example.nexus.app.category.domain;

public enum GenreCategory {
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

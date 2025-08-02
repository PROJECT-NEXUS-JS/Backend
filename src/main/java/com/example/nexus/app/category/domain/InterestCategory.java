package com.example.nexus.app.category.domain;

import lombok.Getter;

@Getter
public enum InterestCategory {
    APP("앱"),
    WEB("웹"),
    GAME("게임"),
    UX_FEEDBACK("UX 피드백"),
    AI("AI"),
    FUNCTION_VALIDATION("기능 검증"),
    SURVEY("설문형 테스트"),
    NEW_SERVICE_LAUNCH("신규 서비스 런칭"),
    HAS_REWARD("리워드 있음"),
    REALTIME_TEST("실시간 테스트 참여"),
    FINTECH("핀테크"),
    FITNESS("피트니스"),
    SOCIAL_COMMUNITY("소셜/커뮤니티"),
    TRAVEL_MOBILITY("여행/모빌리티");

    private final String displayName;

    InterestCategory(String displayName) {
        this.displayName = displayName;
    }
}

package com.example.nexus.app.feedback.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 버그 유형 Enum
 */
@Getter
@RequiredArgsConstructor
public enum BugType {
    UI_UX_ERROR("UI/UX 오류"),
    FUNCTIONAL_ERROR("기능 작동 오류"),
    RESPONSE_SPEED("응답 속도 문제"),
    DATA_INPUT_ERROR("데이터 입력 오류"),
    CRASH("앱/화면 크래시"),
    TYPO("오타/텍스트 오류"),
    NOTIFICATION_ISSUE("알림 문제"),
    OTHER("기타");

    private final String description;
}


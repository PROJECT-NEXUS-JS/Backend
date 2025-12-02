package com.example.nexus.app.feedback.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 불편 요소 타입 Enum
 */
@Getter
@RequiredArgsConstructor
public enum InconvenienceType {
    UI_UX("UI/UX"),
    SPEED("속도"),
    FUNCTION("기능 오류"),
    TEXT_GUIDE("텍스트/가이드"),
    GUIDE("가이드"),
    OTHER("기타");

    private final String description;
}


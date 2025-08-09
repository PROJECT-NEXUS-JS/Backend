package com.example.nexus.app.post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipationStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    COMPLETED("테스트 완료"),
    REJECTED("거절됨");

    private final String description;
}

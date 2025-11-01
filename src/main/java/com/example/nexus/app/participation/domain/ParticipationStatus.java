package com.example.nexus.app.participation.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "참가 신청 상태")
public enum ParticipationStatus {
    @Schema(description = "대기중")
    PENDING("대기중"),

    @Schema(description = "승인됨")
    APPROVED("승인됨"),

    @Schema(description = "테스트 완료")
    COMPLETED("테스트 완료"),

    @Schema(description = "거절됨")
    REJECTED("거절됨");

    private final String description;
}

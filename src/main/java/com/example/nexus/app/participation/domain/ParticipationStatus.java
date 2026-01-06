package com.example.nexus.app.participation.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "참가 신청 상태")
public enum ParticipationStatus {
    @Schema(description = "승인 대기")
    PENDING("승인 대기"),

    @Schema(description = "진행중")
    APPROVED("진행중"),

    // 참여자의 테스트 완료 (피드백 제출)
    @Schema(description = "테스트 완료")
    TEST_COMPLETED("테스트 완료"),

    // 모집자가 확인 후 최종 테스트 완료 및 지급 대기 상태
    @Schema(description = "지급 대기")
    COMPLETED("지급 대기"),

    @Schema(description = "거절됨")
    REJECTED("거절됨");

    private final String description;
}

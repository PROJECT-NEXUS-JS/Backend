package com.example.nexus.app.reward.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "리워드 지급 상태")
public enum RewardStatus {
    @Schema(description = "지급 대기")
    PENDING("지급 대기"),

    @Schema(description = "지급 완료")
    PAID("지급 완료");

    private final String description;
}

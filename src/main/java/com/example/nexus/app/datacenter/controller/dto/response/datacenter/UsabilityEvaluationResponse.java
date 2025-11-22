package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 기능별 사용성 평가 응답 DTO
 */
@Builder
@Schema(description = "기능별 사용성 평가 데이터 (Radar 차트)")
public record UsabilityEvaluationResponse(
        @Schema(description = "기능 작동성 평균 점수 (1-5점)")
        Double functionalityScore,

        @Schema(description = "이해도 평균 점수 (1-5점)")
        Double comprehensibilityScore,

        @Schema(description = "로딩 속도 평균 점수 (1-5점)")
        Double loadingSpeedScore,

        @Schema(description = "반응 타이밍 평균 점수 (1-5점)")
        Double responseTimingScore,

        @Schema(description = "안정성 평균 점수 (1-5점)")
        Double stabilityScore
) {
}


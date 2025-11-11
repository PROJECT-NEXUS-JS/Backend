package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Map;

/**
 * 전반 평가 응답 DTO
 */
@Builder
@Schema(description = "전반 평가 데이터")
public record OverallEvaluationResponse(
        @Schema(description = "평균 만족도 점수 (1-5점)")
        Double averageSatisfaction,

        @Schema(description = "평균 추천 의향 점수 (1-5점)")
        Double averageRecommendation,

        @Schema(description = "평균 재이용 의향 점수 (1-5점)")
        Double averageReuse,

        @Schema(description = "만족도 점수 분포 (1~5점: 개수)")
        Map<Integer, Long> satisfactionDistribution,

        @Schema(description = "추천 의향 분포 (1~5점: 개수)")
        Map<Integer, Long> recommendationDistribution,

        @Schema(description = "재이용 의향 분포 (1~5점: 개수)")
        Map<Integer, Long> reuseDistribution
) {
}


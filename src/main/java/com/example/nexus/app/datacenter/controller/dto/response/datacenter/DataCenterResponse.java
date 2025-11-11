package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 데이터센터 통합 응답 DTO
 */
@Builder
@Schema(description = "데이터센터 전체 데이터")
public record DataCenterResponse(
        @Schema(description = "요약 정보")
        DataCenterSummaryResponse summary,

        @Schema(description = "전반 평가")
        OverallEvaluationResponse overallEvaluation,

        @Schema(description = "품질 피드백")
        QualityFeedbackResponse qualityFeedback,

        @Schema(description = "기능별 사용성 평가")
        UsabilityEvaluationResponse usabilityEvaluation,

        @Schema(description = "개선 제안 및 인사이트")
        InsightsResponse insights
) {
}


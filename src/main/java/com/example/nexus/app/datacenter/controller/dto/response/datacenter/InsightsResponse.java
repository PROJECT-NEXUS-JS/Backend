package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 개선 제안 및 인사이트 응답 DTO
 */
@Builder
@Schema(description = "개선 제안 및 인사이트 데이터")
public record InsightsResponse(
        @Schema(description = "좋았던 점 피드백 리스트")
        List<FeedbackItemDto> positiveFeedbacks,

        @Schema(description = "개선 제안 피드백 리스트")
        List<FeedbackItemDto> improvementSuggestions,

        @Schema(description = "주요 키워드 분석 (키워드: 빈도수)")
        Map<String, Integer> keywords
) {

    @Builder
    public record FeedbackItemDto(
            @Schema(description = "피드백 ID")
            Long feedbackId,

            @Schema(description = "요약 문장 (최대 50자)")
            String summary,

            @Schema(description = "전체 내용")
            String fullContent,

            @Schema(description = "이모지")
            String emoji
    ) {
    }
}


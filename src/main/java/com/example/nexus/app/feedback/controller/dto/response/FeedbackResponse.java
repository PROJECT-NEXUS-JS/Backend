package com.example.nexus.app.feedback.controller.dto.response;

import com.example.nexus.app.feedback.domain.BugType;
import com.example.nexus.app.feedback.domain.Feedback;
import com.example.nexus.app.feedback.domain.InconvenienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Schema(description = "피드백 응답")
public record FeedbackResponse(

        @Schema(description = "피드백 ID", example = "1")
        Long feedbackId,

        @Schema(description = "참여 ID", example = "1")
        Long participationId,

        @Schema(description = "전반적인 만족도 (1-5)", example = "4")
        Integer overallSatisfaction,

        @Schema(description = "추천 의향 (1-5)", example = "5")
        Integer recommendationIntent,

        @Schema(description = "재이용 의향 (1-5)", example = "4")
        Integer reuseIntent,

        @Schema(description = "가장 불편했던 부분")
        InconvenienceType mostInconvenient,

        @Schema(description = "버그 존재 여부", example = "true")
        Boolean hasBug,

        @Schema(description = "발견된 문제 유형들")
        Set<BugType> bugTypes,

        @Schema(description = "문제 발생 위치")
        String bugLocation,

        @Schema(description = "버그 상황 설명")
        String bugDescription,

        @Schema(description = "첨부 스크린샷 URL 리스트")
        List<String> screenshotUrls,

        @Schema(description = "주요 기능 작동성 (1-5)", example = "4")
        Integer functionalityScore,

        @Schema(description = "가이드 이해도 (1-5)", example = "5")
        Integer comprehensibilityScore,

        @Schema(description = "로딩 속도 (1-5)", example = "3")
        Integer speedScore,

        @Schema(description = "알림/반응 타이밍 (1-5)", example = "4")
        Integer responseTimingScore,

        @Schema(description = "좋았던 점")
        String goodPoints,

        @Schema(description = "개선 제안")
        String improvementSuggestions,

        @Schema(description = "기타 자유 의견")
        String additionalComments,

        @Schema(description = "평균 만족도", example = "4.33")
        Double averageSatisfaction,

        @Schema(description = "평균 사용성 점수", example = "4.0")
        Double averageUsabilityScore,

        @Schema(description = "생성일시")
        LocalDateTime createdAt,

        @Schema(description = "수정일시")
        LocalDateTime updatedAt
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getParticipation().getId(),
                feedback.getOverallSatisfaction(),
                feedback.getRecommendationIntent(),
                feedback.getReuseIntent(),
                feedback.getMostInconvenient(),
                feedback.getHasBug(),
                feedback.getBugTypes(),
                feedback.getBugLocation(),
                feedback.getBugDescription(),
                feedback.getScreenshotUrls(),
                feedback.getFunctionalityScore(),
                feedback.getComprehensibilityScore(),
                feedback.getSpeedScore(),
                feedback.getResponseTimingScore(),
                feedback.getGoodPoints(),
                feedback.getImprovementSuggestions(),
                feedback.getAdditionalComments(),
                feedback.getAverageSatisfaction(),
                feedback.getAverageUsabilityScore(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );
    }
}


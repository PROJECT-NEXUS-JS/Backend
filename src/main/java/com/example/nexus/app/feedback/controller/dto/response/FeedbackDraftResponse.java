package com.example.nexus.app.feedback.controller.dto.response;

import com.example.nexus.app.feedback.domain.BugType;
import com.example.nexus.app.feedback.domain.FeedbackDraft;
import com.example.nexus.app.feedback.domain.InconvenienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Schema(description = "피드백 임시저장 응답")
public record FeedbackDraftResponse(

        @Schema(description = "임시저장 ID", example = "1")
        Long draftId,

        @Schema(description = "참여 ID", example = "1")
        Long participationId,

        Integer overallSatisfaction,
        Integer recommendationIntent,
        Integer reuseIntent,
        InconvenienceType mostInconvenient,
        Boolean hasBug,
        Set<BugType> bugTypes,
        String bugLocation,
        String bugDescription,
        List<String> screenshotUrls,
        Integer functionalityScore,
        Integer comprehensibilityScore,
        Integer speedScore,
        Integer responseTimingScore,
        String goodPoints,
        String improvementSuggestions,
        String additionalComments,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FeedbackDraftResponse from(FeedbackDraft draft) {
        return new FeedbackDraftResponse(
                draft.getId(),
                draft.getParticipation().getId(),
                draft.getOverallSatisfaction(),
                draft.getRecommendationIntent(),
                draft.getReuseIntent(),
                draft.getMostInconvenient(),
                draft.getHasBug(),
                draft.getBugTypes(),
                draft.getBugLocation(),
                draft.getBugDescription(),
                draft.getScreenshotUrls(),
                draft.getFunctionalityScore(),
                draft.getComprehensibilityScore(),
                draft.getSpeedScore(),
                draft.getResponseTimingScore(),
                draft.getGoodPoints(),
                draft.getImprovementSuggestions(),
                draft.getAdditionalComments(),
                draft.getCreatedAt(),
                draft.getUpdatedAt()
        );
    }
}


package com.example.nexus.app.feedback.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 피드백 상태 응답")
public record MyFeedbackStatusResponse(

        @Schema(description = "참여 ID", example = "1")
        Long participationId,

        @Schema(description = "피드백 제출 여부", example = "false")
        Boolean hasSubmitted,

        @Schema(description = "임시저장 존재 여부", example = "true")
        Boolean hasDraft,

        @Schema(description = "피드백 정보 (제출된 경우)")
        FeedbackResponse feedback,

        @Schema(description = "임시저장 정보 (초안이 있는 경우)")
        FeedbackDraftResponse draft
) {
    public static MyFeedbackStatusResponse submitted(Long participationId, FeedbackResponse feedback) {
        return new MyFeedbackStatusResponse(participationId, true, false, feedback, null);
    }

    public static MyFeedbackStatusResponse draft(Long participationId, FeedbackDraftResponse draft) {
        return new MyFeedbackStatusResponse(participationId, false, true, null, draft);
    }

    public static MyFeedbackStatusResponse notStarted(Long participationId) {
        return new MyFeedbackStatusResponse(participationId, false, false, null, null);
    }
}


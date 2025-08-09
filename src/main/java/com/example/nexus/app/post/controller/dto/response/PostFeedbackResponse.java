package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.PostFeedback;
import com.example.nexus.app.post.domain.PrivacyItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

public record PostFeedbackResponse(
        @Schema(description = "피드백 방법")
        String feedbackMethod,

        @Schema(description = "피드백 항목")
        List<String> feedbackItems,

        @Schema(description = "개인정보 수집 항목")
        Set<PrivacyItem> privacyItems
) {

    public static PostFeedbackResponse from(PostFeedback feedback) {
        return new PostFeedbackResponse(
                feedback.getFeedbackMethod(),
                feedback.getFeedbackItems(),
                feedback.getPrivacyItems()
        );
    }
}

package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.PostContent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PostContentResponse(
        @Schema(description = "참여 방법")
        String participationMethod,

        @Schema(description = "스토리 가이드")
        String storyGuide,

        @Schema(description = "미디어 URLs")
        List<String> mediaUrls
) {

    public static PostContentResponse from(PostContent content) {
        return new PostContentResponse(
                content.getParticipationMethod(),
                content.getStoryGuide(),
                content.getMediaUrls()
        );
    }
}

package com.example.nexus.app.dashboard.controller.dto.response;

import com.example.nexus.app.post.domain.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostStatusResponse(
        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "변경된 모집 상태")
        PostStatus status
) {
    public static PostStatusResponse of(Long postId, PostStatus status) {
        return new PostStatusResponse(postId, status);
    }
}

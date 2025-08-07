package com.example.nexus.app.dashboard.controller.dto.response;

import com.example.nexus.app.post.domain.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MyPostSummaryResponse(
        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "게시글 제목")
        String title,

        @Schema(description = "모집 상태")
        PostStatus recruitmentStatus,

        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
    public static MyPostSummaryResponse of(Long postId, String title, PostStatus status, LocalDateTime createdAt) {
        return new MyPostSummaryResponse(postId, title, status, createdAt);
    }
}

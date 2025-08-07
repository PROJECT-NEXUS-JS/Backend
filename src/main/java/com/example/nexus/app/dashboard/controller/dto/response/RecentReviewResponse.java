package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecentReviewResponse(
        @Schema(description = "리뷰 ID")
        Long reviewId,

        @Schema(description = "리뷰어 닉네임")
        String reviewerNickname,

        @Schema(description = "리뷰어 프로필 이미지 URL")
        String reviewerProfileImageUrl,

        @Schema(description = "평점")
        Integer rating,

        @Schema(description = "리뷰 내용")
        String content,

        @Schema(description = "작성일시")
        LocalDateTime createdAt
) {
    public static RecentReviewResponse of(Long reviewId, String reviewerNickname, String reviewerProfileImageUrl,
                                          Integer rating, String content, LocalDateTime createdAt) {
        return new RecentReviewResponse(reviewId, reviewerNickname, reviewerProfileImageUrl, rating, content, createdAt);
    }
}

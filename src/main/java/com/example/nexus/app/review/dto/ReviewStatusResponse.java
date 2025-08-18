package com.example.nexus.app.review.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 리뷰 작성 상태 응답 DTO
 */
@Getter
@Builder
public class ReviewStatusResponse {

    private Long postId;
    private Boolean canWriteReview;
    private Boolean hasWrittenReview;
    private ReviewInfo existingReview;

    @Getter
    @Builder
    public static class ReviewInfo {
        private Long reviewId;
        private Integer rating;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}

package com.example.nexus.app.review.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 작성한 리뷰 응답 DTO
 */
@Getter
@Builder
public class WrittenReviewResponse {

    private Long reviewId;
    private Long postId;
    private String postTitle;
    private String postThumbnail;
    private String category;
    private Integer rating;
    private String content;
    private LocalDateTime reviewCreatedAt;
    private LocalDateTime reviewUpdatedAt;
    private Boolean canEdit;

    @Getter
    @Builder
    public static class PostInfo {
        private Long id;
        private String title;
        private String thumbnail;
        private String category;
    }
}

package com.example.nexus.app.review.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 작성 가능한 리뷰 응답 DTO
 */
@Getter
@Builder
public class WritableReviewResponse {

    private Long postId;
    private String postTitle;
    private String postThumbnail;
    private String category;
    private LocalDateTime approvedAt;
    private Boolean canWriteReview;

}

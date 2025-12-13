package com.example.nexus.app.review.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 리뷰 답글 응답 DTO
 */
@Getter
@Builder
public class ReviewReplyResponse {

    private Long id;
    private Long reviewId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 작성자 정보 (내부 클래스로 구성)
    private WriterInfo writer;

    @Getter
    @Builder
    public static class WriterInfo {

        private Long id;
        private String nickname;
        private String profileUrl;

    }

}


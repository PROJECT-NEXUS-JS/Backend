package com.example.nexus.app.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 답글 생성 요청 DTO
 * - reviewId는 PathVariable로 받으므로 DTO에서 제외
 */
@Getter
@Setter
public class ReviewReplyCreateRequest {
    
    @NotBlank(message = "답글 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "답글 내용은 1자 이상 1000자 이하여야 합니다.")
    private String content;

}


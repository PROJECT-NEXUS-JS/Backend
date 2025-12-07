package com.example.nexus.app.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 답글 생성 요청 DTO
 */
@Getter
@Setter
public class ReviewReplyCreateRequest {

    @NotNull(message = "리뷰 ID는 필수입니다.")
    private Long reviewId;
    
    @NotBlank(message = "답글 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "답글 내용은 1자 이상 1000자 이하여야 합니다.")
    private String content;

}


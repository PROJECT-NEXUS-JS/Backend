package com.example.nexus.app.review.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 생성 요청 DTO
 */
@Getter
@Setter
public class ReviewCreateRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;
    
    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 최소 1점입니다.")
    @Max(value = 5, message = "평점은 최대 5점입니다.")
    private Integer rating;
    
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 10, max = 1000, message = "리뷰 내용은 10자 이상 1000자 이하여야 합니다.")
    private String content;

}

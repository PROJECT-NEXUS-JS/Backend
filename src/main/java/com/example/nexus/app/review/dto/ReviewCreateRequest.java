package com.example.nexus.app.review.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 생성 요청 DTO
 */
@Getter
@Setter
public class ReviewCreateRequest {

    private Long postId;
    private Integer rating;
    private String content;

} 
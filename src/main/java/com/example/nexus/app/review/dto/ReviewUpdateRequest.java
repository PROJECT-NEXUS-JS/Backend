package com.example.nexus.app.review.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 리뷰 수정 요청 DTO
 */
@Getter
@Setter
public class ReviewUpdateRequest {

    private Integer rating;
    private String content;

} 
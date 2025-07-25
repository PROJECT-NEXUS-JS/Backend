package com.example.nexus.app.review.controller;

import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewResponse;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.service.ReviewService;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;

/**
 * ReviewController
 * - 리뷰 CRUD API 엔드포인트
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "게시글에 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
        @RequestBody ReviewCreateRequest request,
        @AuthenticationPrincipal User user
    ) {
        Review review = reviewService.createReview(request, user);
        return ResponseEntity.status(201)
            .body(ApiResponse.onSuccess(toResponse(review)));
    }

    @Operation(summary = "리뷰 하나만 조회", description = "리뷰 ID로 단일 리뷰를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
        @PathVariable Long id
    ) {
        Review review = reviewService.getReview(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.onSuccess(toResponse(review)));
    }

    @Operation(summary = "게시글 별 리뷰 목록 조회", description = "게시글 ID로 해당 게시글의 모든 리뷰를 조회합니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByPostId(
        @PathVariable Long postId
    ) {
        List<ReviewResponse> responses = reviewService.getReviewsByPostId(postId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @Operation(summary = "리뷰 수정", description = "리뷰 작성자만 리뷰를 수정할 수 있습니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
        @PathVariable Long id,
        @RequestBody ReviewUpdateRequest request,
        @AuthenticationPrincipal User user
    ) {
        Review review = reviewService.getReview(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        if (!review.getCreatedBy().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        Review updated = reviewService.updateReview(id, request, user)
            .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.onSuccess(toResponse(updated)));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 작성자만 리뷰를 삭제할 수 있습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
        @PathVariable Long id,
        @AuthenticationPrincipal User user
    ) {
        Review review = reviewService.getReview(id)
            .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));
        if (!review.getCreatedBy().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
            .id(review.getId())
            .postId(review.getPostId())
            .rating(review.getRating())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .writer(ReviewResponse.WriterInfo.builder()
                .id(review.getCreatedBy().getId())
                .nickname(review.getCreatedBy().getNickname())
                .profileUrl(review.getCreatedBy().getProfileUrl())
                .build())
            .build();
    }

} 
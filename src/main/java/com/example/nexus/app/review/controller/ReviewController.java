package com.example.nexus.app.review.controller;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewResponse;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.service.ReviewService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ReviewController
 * - 리뷰 CRUD API 엔드포인트
 */
@Tag(name = "리뷰", description = "리뷰 관련 API")
@RestController
@RequestMapping("/v1/users/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "게시글에 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Review review = reviewService.createReview(request, userDetails.getUserId());
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

    @Operation(summary = "게시글 별 리뷰 목록 조회 (페이징)", description = "게시글 ID로 해당 게시글의 리뷰를 페이징하여 조회합니다.")
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByPostId(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "정렬 기준 (latest, rating_desc, rating_asc)") 
            @RequestParam(defaultValue = "latest") String sortBy,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Review> reviews = reviewService.getReviewsByPostIdWithPaging(postId, sortBy, pageable);
        Page<ReviewResponse> responses = reviews.map(this::toResponse);
        return ResponseEntity.ok(ApiResponse.onSuccess(responses));
    }

    @Operation(summary = "게시글 별 리뷰 목록 조회 (전체)", description = "게시글 ID로 해당 게시글의 모든 리뷰를 조회합니다.")
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviewsByPostId(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Review updated = reviewService.updateReview(id, request, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.onSuccess(toResponse(updated)));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 작성자만 리뷰를 삭제할 수 있습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        reviewService.deleteReview(id, userDetails.getUserId());
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

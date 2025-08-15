package com.example.nexus.app.review.controller;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewResponse;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.dto.WritableReviewResponse;
import com.example.nexus.app.review.dto.WrittenReviewResponse;
import com.example.nexus.app.review.dto.ReviewStatusResponse;
import com.example.nexus.app.review.service.ReviewService;
import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * ReviewController
 * - 리뷰 CRUD API 엔드포인트
 */
@Tag(name = "리뷰", description = "리뷰 관련 api")
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

    @Operation(summary = "작성 가능한 리뷰 목록 조회", description = "사용자가 참여했지만 아직 리뷰를 작성하지 않은 게시글 목록을 조회합니다.")
    @GetMapping("/writable")
    public ResponseEntity<ApiResponse<Page<WritableReviewResponse>>> getWritableReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Page<WritableReviewResponse> response = reviewService.getWritableReviews(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "작성한 리뷰 목록 조회", description = "사용자가 이미 리뷰를 작성한 게시글 목록을 조회합니다.")
    @GetMapping("/written")
    public ResponseEntity<ApiResponse<Page<WrittenReviewResponse>>> getWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        Page<WrittenReviewResponse> response = reviewService.getWrittenReviews(userDetails.getUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @Operation(summary = "리뷰 작성 상태 확인", description = "특정 게시글에 대한 리뷰 작성 가능 여부를 확인합니다.")
    @GetMapping("/status/{postId}")
    public ResponseEntity<ApiResponse<ReviewStatusResponse>> getReviewStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        ReviewStatusResponse response = reviewService.getReviewStatus(userDetails.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
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

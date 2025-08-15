package com.example.nexus.app.review.service;

import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.dto.WritableReviewResponse;
import com.example.nexus.app.review.dto.WrittenReviewResponse;
import com.example.nexus.app.review.dto.ReviewStatusResponse;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * ReviewService
 * - 리뷰 생성, 조회, 수정, 삭제 등 비즈니스 로직 담당
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(ReviewCreateRequest request, Long authUserId) {
        User currentUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Review review = Review.builder()
                .postId(request.getPostId())
                .rating(request.getRating())
                .content(request.getContent())
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Optional<Review> getReview(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByPostId(Long postId) {
        return reviewRepository
                .findByPostId(postId);
    }

    @Transactional
    public Review updateReview(Long reviewId, ReviewUpdateRequest request, Long authUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        if (!review.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        User currentUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        review.update(request.getRating(), request.getContent(), currentUser);
        return review;
    }

    @Transactional
    public void deleteReview(Long reviewId, Long authUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        if (!review.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        reviewRepository.deleteById(reviewId);
    }

    // 작성 가능한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<WritableReviewResponse> getWritableReviews(Long userId, Pageable pageable) {
        // 사용자가 참여 승인된 게시글 목록 조회
        Page<com.example.nexus.app.post.domain.Participation> participations = 
            participationRepository.findByUserIdAndStatusWithPost(userId, ParticipationStatus.APPROVED, pageable);

        // 사용자가 이미 리뷰를 작성한 게시글 ID 목록 조회
        List<Long> reviewedPostIds = reviewRepository.findPostIdsByCreatedBy(userId);

        return participations.map(participation -> {
            Long postId = participation.getPost().getId();
            boolean canWriteReview = !reviewedPostIds.contains(postId);

            return WritableReviewResponse.builder()
                    .postId(postId)
                    .postTitle(participation.getPost().getTitle())
                    .postThumbnail(participation.getPost().getThumbnailUrl())
                    .category(participation.getPost().getMainCategory().iterator().next().name())
                    .approvedAt(participation.getApprovedAt())
                    .canWriteReview(canWriteReview)
                    .build();
        });
    }

    // 작성한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<WrittenReviewResponse> getWrittenReviews(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByCreatedByOrderByCreatedAtDesc(userId, pageable);

        return reviews.map(review -> {
            // Post 정보 조회
            com.example.nexus.app.post.domain.Post post = postRepository.findById(review.getPostId())
                    .orElse(null);

            return WrittenReviewResponse.builder()
                    .reviewId(review.getId())
                    .postId(review.getPostId())
                    .postTitle(post != null ? post.getTitle() : "삭제된 게시글")
                    .postThumbnail(post != null ? post.getThumbnailUrl() : null)
                    .category(post != null && !post.getMainCategory().isEmpty() ? 
                            post.getMainCategory().iterator().next().name() : "카테고리 없음")
                    .rating(review.getRating())
                    .content(review.getContent())
                    .reviewCreatedAt(review.getCreatedAt())
                    .reviewUpdatedAt(review.getUpdatedAt())
                    .canEdit(true) // TODO: 수정 가능 여부 로직 추가
                    .build();
        });
    }

    // 리뷰 작성 상태 확인
    @Transactional(readOnly = true)
    public ReviewStatusResponse getReviewStatus(Long userId, Long postId) {
        // 사용자가 해당 게시글에 참여했는지 확인
        boolean hasParticipated = participationRepository.existsByUserIdAndPostIdAndStatus(
                userId, postId, ParticipationStatus.APPROVED);

        // 사용자가 해당 게시글에 리뷰를 작성했는지 확인
        Optional<Review> existingReview = reviewRepository.findByCreatedByAndPostId(userId, postId);

        boolean canWriteReview = hasParticipated && existingReview.isEmpty();
        boolean hasWrittenReview = existingReview.isPresent();

        ReviewStatusResponse.ReviewInfo reviewInfo = null;
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            reviewInfo = ReviewStatusResponse.ReviewInfo.builder()
                    .reviewId(review.getId())
                    .rating(review.getRating())
                    .content(review.getContent())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }

        return ReviewStatusResponse.builder()
                .postId(postId)
                .canWriteReview(canWriteReview)
                .hasWrittenReview(hasWrittenReview)
                .existingReview(reviewInfo)
                .build();
    }
}

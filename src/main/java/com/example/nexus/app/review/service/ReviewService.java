package com.example.nexus.app.review.service;

import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.repository.ReviewRepository;
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

/**
 * ReviewService
 * - 리뷰 생성, 조회, 수정, 삭제 등 비즈니스 로직 담당
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
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

    @Transactional(readOnly = true)
    public Page<Review> getReviewsByPostIdWithPaging(Long postId, String sortBy, Pageable pageable) {
        return switch (sortBy) {
            case "rating_desc" -> reviewRepository.findByPostIdOrderByRatingDescCreatedAtDesc(postId, pageable);
            case "rating_asc" -> reviewRepository.findByPostIdOrderByRatingAscCreatedAtDesc(postId, pageable);
            default -> reviewRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable); // latest
        };
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
}

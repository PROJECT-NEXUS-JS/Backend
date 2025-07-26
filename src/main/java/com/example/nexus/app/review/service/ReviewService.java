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
    public Review createReview(ReviewCreateRequest request, User authUser) {
        User currentUser = userRepository.findById(authUser.getId())
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
    public Review updateReview(Long reviewId, ReviewUpdateRequest request, User authUser) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.RESOURCE_NOT_FOUND));

        if (!review.getCreatedBy().getId().equals(authUser.getId())) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        User currentUser = userRepository.findById(authUser.getId())
            .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        review.update(request.getRating(), request.getContent(), currentUser);
        return review;
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
        }
        reviewRepository.deleteById(reviewId);
    }

}

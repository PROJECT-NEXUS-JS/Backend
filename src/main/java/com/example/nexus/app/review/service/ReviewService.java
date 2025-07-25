package com.example.nexus.app.review.service;

import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.user.domain.User;
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

    @Transactional
    public Review createReview(ReviewCreateRequest request, User user) {
        Review review = buildReview(request, user);
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
    public Optional<Review> updateReview(Long reviewId, ReviewUpdateRequest request, User user) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return Optional.empty();
        }
        Review review = optionalReview.get();
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());
        review.setUpdatedBy(user);
        return Optional.of(review);
    }

    @Transactional
    public boolean deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            return false;
        }
        reviewRepository.deleteById(reviewId);
        return true;
    }

    private Review buildReview(ReviewCreateRequest request, User user) {
        Review review = new Review();
        review.setPostId(request.getPostId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setCreatedBy(user);
        review.setUpdatedBy(user);
        return review;
    }

} 
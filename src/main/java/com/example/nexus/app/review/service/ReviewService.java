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
 * - CRUD 로직 담당
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 생성
     */
    @Transactional
    public Review createReview(ReviewCreateRequest request, User user) {
        Review review = new Review();
        review.setPostId(request.getPostId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setCreatedBy(user);
        review.setUpdatedBy(user);
        return reviewRepository.save(review);
    }

    /**
     * 리뷰 하나 조회
     */
    @Transactional(readOnly = true)
    public Optional<Review> getReview(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    /**
     * 게시글 별 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByPostId(Long postId) {
        return reviewRepository.findByPostId(postId);
    }

    /**
     * 리뷰 수정 (작성자만 가능, 권한 체크는 Controller에서 처리)
     */
    @Transactional
    public Optional<Review> updateReview(Long reviewId, ReviewUpdateRequest request, User user) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setUpdatedAt(LocalDateTime.now());
            review.setUpdatedBy(user);
            return Optional.of(review);
        }
        return Optional.empty();
    }

    /**
     * 리뷰 삭제 (작성자만 가능, 권한 체크는 Controller에서 처리)
     */
    @Transactional
    public boolean deleteReview(Long reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }
} 
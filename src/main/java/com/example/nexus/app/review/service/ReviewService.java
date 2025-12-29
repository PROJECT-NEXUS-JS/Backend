package com.example.nexus.app.review.service;

import com.example.nexus.app.badge.domain.BadgeConditionType;
import com.example.nexus.app.badge.service.BadgeService;
import com.example.nexus.app.review.domain.Review;
import com.example.nexus.app.review.domain.ReviewReply;
import com.example.nexus.app.review.dto.ReviewCreateRequest;
import com.example.nexus.app.review.dto.ReviewResponse;
import com.example.nexus.app.review.dto.ReviewUpdateRequest;
import com.example.nexus.app.review.dto.ReviewReplyCreateRequest;
import com.example.nexus.app.review.dto.ReviewReplyResponse;
import com.example.nexus.app.review.dto.ReviewReplyUpdateRequest;
import com.example.nexus.app.review.dto.WritableReviewResponse;
import com.example.nexus.app.review.dto.WrittenReviewResponse;
import com.example.nexus.app.review.dto.ReviewStatusResponse;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.review.repository.ReviewReplyRepository;
import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.domain.Post;
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
import java.util.stream.Collectors;

/**
 * ReviewService
 * - 리뷰 생성, 조회, 수정, 삭제 등 비즈니스 로직 담당
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository reviewReplyRepository;
    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, Long authUserId) {
        User currentUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 게시글 존재 확인
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 참여 여부 확인
        boolean hasParticipated = participationRepository.existsByUserIdAndPostIdAndStatus(
                authUserId, request.getPostId(), ParticipationStatus.APPROVED);
        if (!hasParticipated) {
            throw new GeneralException(ErrorStatus.NOT_PARTICIPATED);
        }

        // 중복 리뷰 확인
        if (reviewRepository.findByCreatedByAndPostId(authUserId, request.getPostId()).isPresent()) {
            throw new GeneralException(ErrorStatus.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .postId(request.getPostId())
                .rating(request.getRating())
                .content(request.getContent())
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();
        Review savedReview = reviewRepository.save(review);

        // 뱃지 부여 체크 - 리뷰어의 리뷰 작성 뱃지
        badgeService.checkAndAwardBadge(authUserId, BadgeConditionType.REVIEW_CREATED);

        // 뱃지 부여 체크 - 테스트 완료 뱃지 (리뷰 작성 = 테스트 완료)
        badgeService.checkAndAwardBadge(authUserId, BadgeConditionType.PARTICIPATION_COMPLETED);

        // 뱃지 부여 체크 - 특정 게시글의 첫 리뷰 작성 뱃지
        badgeService.checkAndAwardFirstReviewBadge(authUserId, request.getPostId());

        // 뱃지 부여 체크 - 기획자의 소통 뱃지 (리뷰 수신)
        badgeService.checkAndAwardReviewReceivedBadge(post.getCreatedBy());

        return toReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findByIdWithCreatedBy(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));
        return toReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByPostId(Long postId) {
        List<Review> reviews = reviewRepository.findByPostId(postId);
        
        List<Long> reviewIds = reviews.stream()
                .map(Review::getId)
                .collect(Collectors.toList());
        
        // 리뷰 ID별 답글 개수 Map 생성
        java.util.Map<Long, Long> replyCountMap = java.util.Collections.emptyMap();
        if (!reviewIds.isEmpty()) {
            replyCountMap = reviewReplyRepository.countByReviewIds(reviewIds)
                    .stream()
                    .collect(Collectors.toMap(
                            result -> ((Number) result[0]).longValue(),
                            result -> ((Number) result[1]).longValue()
                    ));
        }
        
        final java.util.Map<Long, Long> finalReplyCountMap = replyCountMap;
        
        return reviews.stream()
                .map(review -> {
                    boolean hasReply = finalReplyCountMap.containsKey(review.getId()) 
                            && finalReplyCountMap.get(review.getId()) > 0;
                    return toReviewResponse(review, hasReply);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, Long authUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        if (!review.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        User currentUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        review.update(request.getRating(), request.getContent(), currentUser);
        return toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long authUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        if (!review.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        reviewRepository.deleteById(reviewId);
    }

    // 작성 가능한 리뷰 목록 조회
    @Transactional(readOnly = true)
    public Page<WritableReviewResponse> getWritableReviews(Long userId, Pageable pageable) {
        // 사용자가 참여 승인된 게시글 목록 조회
        Page<Participation> participations =
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

        // Post ID 목록 추출 및 한 번에 조회 (N+1 문제 해결)
        Set<Long> postIds = reviews.getContent().stream()
                .map(Review::getPostId)
                .collect(Collectors.toSet());
        
        java.util.Map<Long, com.example.nexus.app.post.domain.Post> postMap = 
                postRepository.findAllById(postIds).stream()
                        .collect(Collectors.toMap(
                                com.example.nexus.app.post.domain.Post::getId, 
                                post -> post
                        ));

        return reviews.map(review -> {
            com.example.nexus.app.post.domain.Post post = postMap.get(review.getPostId());

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
                    .canEdit(canEditReview(review))
                    .build();
        });
    }

    // 리뷰 수정 가능 여부 확인 (작성 후 30일 이내)
    private boolean canEditReview(Review review) {
        return review.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30));
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

    private ReviewResponse toReviewResponse(Review review) {
        return toReviewResponse(review, false);
    }

    private ReviewResponse toReviewResponse(Review review, boolean hasReply) {
        User createdBy = review.getCreatedBy();
        
        return ReviewResponse.builder()
                .id(review.getId())
                .postId(review.getPostId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .hasReply(hasReply)
                .writer(ReviewResponse.WriterInfo.builder()
                        .id(createdBy.getId())
                        .nickname(createdBy.getNickname() != null ? createdBy.getNickname() : "")
                        .profileUrl(createdBy.getProfileUrl() != null ? createdBy.getProfileUrl() : "")
                        .build())
                .build();
    }

    @Transactional
    public ReviewReplyResponse createReviewReply(Long reviewId, ReviewReplyCreateRequest request, Long authUserId) {
        User currentUser = userRepository.findById(authUserId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 리뷰 존재 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_NOT_FOUND));

        // 게시글 존재 확인 및 작성자 권한 검증
        Post post = postRepository.findById(review.getPostId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        // 게시글 작성자만 답글을 작성할 수 있음
        if (!post.getCreatedBy().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        ReviewReply reply = ReviewReply.builder()
                .content(request.getContent())
                .review(review)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();
        
        ReviewReply savedReply = reviewReplyRepository.save(reply);
        return toReviewReplyResponse(savedReply);
    }

    @Transactional(readOnly = true)
    public List<ReviewReplyResponse> getReviewReplies(Long reviewId) {
        return reviewReplyRepository
                .findByReviewIdOrderByCreatedAtAsc(reviewId)
                .stream()
                .map(this::toReviewReplyResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewReplyResponse getReviewReply(Long replyId) {
        ReviewReply reply = reviewReplyRepository.findByIdWithCreatedByAndReview(replyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_REPLY_NOT_FOUND));
        return toReviewReplyResponse(reply);
    }

    @Transactional
    public ReviewReplyResponse updateReviewReply(Long replyId, ReviewReplyUpdateRequest request, Long authUserId) {
        ReviewReply reply = reviewReplyRepository.findByIdWithCreatedByAndReview(replyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_REPLY_NOT_FOUND));

        if (!reply.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        // 권한 체크를 통과했으므로 reply.getCreatedBy()를 재사용 (이미 로드됨)
        reply.update(request.getContent(), reply.getCreatedBy());
        return toReviewReplyResponse(reply);
    }

    @Transactional
    public void deleteReviewReply(Long replyId, Long authUserId) {
        ReviewReply reply = reviewReplyRepository.findByIdWithCreatedByAndReview(replyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REVIEW_REPLY_NOT_FOUND));

        if (!reply.getCreatedBy().getId().equals(authUserId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }

        reviewReplyRepository.delete(reply);
    }

    private ReviewReplyResponse toReviewReplyResponse(ReviewReply reply) {
        User createdBy = reply.getCreatedBy();
        
        return ReviewReplyResponse.builder()
                .id(reply.getId())
                .reviewId(reply.getReview().getId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .writer(ReviewReplyResponse.WriterInfo.builder()
                        .id(createdBy.getId())
                        .nickname(createdBy.getNickname() != null ? createdBy.getNickname() : "")
                        .profileUrl(createdBy.getProfileUrl() != null ? createdBy.getProfileUrl() : "")
                        .build())
                .build();
    }
}

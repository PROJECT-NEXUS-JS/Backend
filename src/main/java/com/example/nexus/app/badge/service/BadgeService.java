package com.example.nexus.app.badge.service;

import com.example.nexus.app.badge.domain.Badge;
import com.example.nexus.app.badge.domain.BadgeConditionType;
import com.example.nexus.app.badge.domain.BadgeName;
import com.example.nexus.app.badge.domain.UserBadge;
import com.example.nexus.app.badge.repository.BadgeRepository;
import com.example.nexus.app.badge.repository.UserBadgeRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.post.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 뱃지 서비스
 * - 뱃지 부여 및 조회 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;

    /**
     * 특정 조건에 따라 뱃지 부여 확인 및 부여
     * @param userId 사용자 ID
     * @param conditionType 조건 타입
     */
    @Transactional
    public void checkAndAwardBadge(Long userId, BadgeConditionType conditionType) {
        User user = getUserOrThrow(userId);
        
        // 해당 조건 타입에 해당하는 모든 뱃지 조회
        List<BadgeName> eligibleBadges = BadgeName.getByConditionType(conditionType);
        
        for (BadgeName badgeName : eligibleBadges) {
            // 이미 보유한 뱃지는 스킵
            if (userBadgeRepository.existsByUserIdAndBadgeName(userId, badgeName)) {
                continue;
            }
            
            // 조건 확인
            if (checkBadgeCondition(userId, badgeName)) {
                awardBadge(user, badgeName);
            }
        }
    }

    /**
     * 특정 게시글에 대한 첫 리뷰 작성 시 뱃지 부여
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     */
    @Transactional
    public void checkAndAwardFirstReviewBadge(Long userId, Long postId) {
        User user = getUserOrThrow(userId);
        
        // 해당 게시글의 첫 리뷰인지 확인
        long reviewCount = reviewRepository.countByPostId(postId);
        
        // 첫 리뷰인 경우 (방금 작성한 리뷰가 1개째)
        if (reviewCount == 1) {
            BadgeName pioneerBadge = BadgeName.PIONEER;
            if (!userBadgeRepository.existsByUserIdAndBadgeName(userId, pioneerBadge)) {
                awardBadge(user, pioneerBadge);
            }
        }
    }

    /**
     * 기획자가 리뷰를 받았을 때 뱃지 체크 (소통 뱃지)
     * @param postCreatorId 게시글 작성자(기획자) ID
     */
    @Transactional
    public void checkAndAwardReviewReceivedBadge(Long postCreatorId) {
        User user = getUserOrThrow(postCreatorId);
        
        // 해당 사용자가 작성한 모든 게시글에 달린 총 리뷰 수 조회
        long totalReviewCount = reviewRepository.countReviewsByPostCreator(postCreatorId);
        
        // 소통 뱃지 체크
        List<BadgeName> communicationBadges = BadgeName.getByConditionType(BadgeConditionType.REVIEW_RECEIVED);
        
        for (BadgeName badgeName : communicationBadges) {
            if (!userBadgeRepository.existsByUserIdAndBadgeName(postCreatorId, badgeName)) {
                if (totalReviewCount >= badgeName.getConditionValue()) {
                    awardBadge(user, badgeName);
                }
            }
        }
    }

    /**
     * 기획자가 테스터를 승인했을 때 뱃지 체크 (모집가 뱃지)
     * @param postCreatorId 게시글 작성자(기획자) ID
     */
    @Transactional
    public void checkAndAwardTesterCountBadge(Long postCreatorId) {
        User user = getUserOrThrow(postCreatorId);
        
        // 해당 사용자가 작성한 모든 게시글의 승인된 테스터 수 조회
        long totalTesterCount = participationRepository.countApprovedParticipantsByPostCreator(postCreatorId);
        
        // 모집가 뱃지 체크
        List<BadgeName> recruiterBadges = BadgeName.getByConditionType(BadgeConditionType.TESTER_COUNT_INCREASED);
        
        for (BadgeName badgeName : recruiterBadges) {
            if (!userBadgeRepository.existsByUserIdAndBadgeName(postCreatorId, badgeName)) {
                if (totalTesterCount >= badgeName.getConditionValue()) {
                    awardBadge(user, badgeName);
                }
            }
        }
    }

    /**
     * 사용자의 뱃지 목록 조회
     * @param userId 사용자 ID
     * @return 사용자가 보유한 뱃지 목록
     */
    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findAllByUserIdWithBadge(userId);
    }

    /**
     * 뱃지 조건 체크
     */
    private boolean checkBadgeCondition(Long userId, BadgeName badgeName) {
        BadgeConditionType conditionType = badgeName.getConditionType();
        Integer conditionValue = badgeName.getConditionValue();
        
        return switch (conditionType) {
            case PARTICIPATION_APPROVED -> {
                long count = participationRepository.countApprovedParticipationsByUserId(userId);
                yield count >= conditionValue;
            }
            case PARTICIPATION_COMPLETED -> {
                long count = participationRepository.countCompletedParticipationsByUserId(userId);
                yield count >= conditionValue;
            }
            case REVIEW_CREATED -> {
                long count = reviewRepository.countByCreatedById(userId);
                yield count >= conditionValue;
            }
            case POST_PUBLISHED -> {
                long count = postRepository.countActivePostsByUserId(userId);
                yield count >= conditionValue;
            }
            case FIRST_REVIEW_ON_POST, REVIEW_RECEIVED, TESTER_COUNT_INCREASED -> 
                // 이 조건들은 별도 메서드에서 처리
                false;
        };
    }

    /**
     * 뱃지 부여
     */
    @Transactional
    public void awardBadge(User user, BadgeName badgeName) {
        // 뱃지 조회 또는 생성
        Badge badge = badgeRepository.findByBadgeName(badgeName)
                .orElseGet(() -> {
                    Badge newBadge = Badge.builder()
                            .badgeName(badgeName)
                            .iconUrl(null) // 아이콘 URL은 별도 설정
                            .build();
                    return badgeRepository.save(newBadge);
                });
        
        // 중복 체크
        if (userBadgeRepository.existsByUserIdAndBadgeName(user.getId(), badgeName)) {
            log.warn("User {} already has badge {}", user.getId(), badgeName);
            return;
        }
        
        // 사용자 뱃지 부여
        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .build();
        
        userBadgeRepository.save(userBadge);
        log.info("Awarded badge {} to user {}", badgeName.getDisplayName(), user.getId());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }
}


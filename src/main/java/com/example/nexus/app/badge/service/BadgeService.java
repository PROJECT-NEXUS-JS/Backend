package com.example.nexus.app.badge.service;

import com.example.nexus.app.badge.domain.Badge;
import com.example.nexus.app.badge.domain.BadgeConditionType;
import com.example.nexus.app.badge.domain.BadgeName;
import com.example.nexus.app.badge.domain.UserBadge;
import com.example.nexus.app.badge.dto.UserBadgeResponse;
import com.example.nexus.app.badge.dto.UserBadgeSummaryResponse;
import com.example.nexus.app.badge.repository.BadgeRepository;
import com.example.nexus.app.badge.repository.UserBadgeRepository;
import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.participation.repository.ParticipationRepository;
import com.example.nexus.app.post.repository.PostRepository;
import com.example.nexus.app.review.repository.ReviewRepository;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        
        List<BadgeName> eligibleBadges = BadgeName.getByConditionType(conditionType);
        
        Set<BadgeName> ownedBadgeNames = userBadgeRepository.findAllByUserIdWithBadge(userId)
                .stream()
                .map(userBadge -> userBadge.getBadge().getBadgeName())
                .collect(Collectors.toSet());
        
        for (BadgeName badgeName : eligibleBadges) {
            if (ownedBadgeNames.contains(badgeName)) {
                continue;
            }
            
            if (checkBadgeCondition(userId, badgeName)) {
                awardBadge(user, badgeName);
                ownedBadgeNames.add(badgeName);
            }
        }
    }

    /**
     * 특정 게시글에 대한 첫 리뷰 작성 시 뱃지 부여
     * 동시성 제어: synchronized 블록으로 race condition 방지
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     */
    @Transactional
    public void checkAndAwardFirstReviewBadge(Long userId, Long postId) {
        User user = getUserOrThrow(userId);
        
        // 동시성 제어: postId별로 락을 획득하여 race condition 방지
        String lockKey = ("FIRST_REVIEW_LOCK:" + postId).intern();
        synchronized (lockKey) {
            long reviewCount = reviewRepository.countByPostId(postId);
            
            if (reviewCount == 1) {
                BadgeName pioneerBadge = BadgeName.PIONEER;
                if (!userBadgeRepository.existsByUserIdAndBadgeName(userId, pioneerBadge)) {
                    awardBadge(user, pioneerBadge);
                }
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
        
        long totalReviewCount = reviewRepository.countReviewsByPostCreator(postCreatorId);
        
        List<BadgeName> communicationBadges = BadgeName.getByConditionType(BadgeConditionType.REVIEW_RECEIVED);
        
        Set<BadgeName> ownedBadgeNames = userBadgeRepository.findAllByUserIdWithBadge(postCreatorId)
                .stream()
                .map(userBadge -> userBadge.getBadge().getBadgeName())
                .collect(Collectors.toSet());
        
        for (BadgeName badgeName : communicationBadges) {
            if (!ownedBadgeNames.contains(badgeName)) {
                if (totalReviewCount >= badgeName.getConditionValue()) {
                    awardBadge(user, badgeName);
                    ownedBadgeNames.add(badgeName);
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
        
        long totalTesterCount = participationRepository.countApprovedParticipantsByPostCreator(postCreatorId);
        
        List<BadgeName> recruiterBadges = BadgeName.getByConditionType(BadgeConditionType.TESTER_COUNT_INCREASED);
        
        Set<BadgeName> ownedBadgeNames = userBadgeRepository.findAllByUserIdWithBadge(postCreatorId)
                .stream()
                .map(userBadge -> userBadge.getBadge().getBadgeName())
                .collect(Collectors.toSet());
        
        for (BadgeName badgeName : recruiterBadges) {
            if (!ownedBadgeNames.contains(badgeName)) {
                if (totalTesterCount >= badgeName.getConditionValue()) {
                    awardBadge(user, badgeName);
                    ownedBadgeNames.add(badgeName);
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
     * 사용자의 뱃지 요약 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 뱃지 요약 응답
     */
    public UserBadgeSummaryResponse getUserBadgeSummary(Long userId) {
        List<UserBadge> userBadges = getUserBadges(userId);
        
        List<UserBadgeResponse> badgeResponses = userBadges.stream()
                .map(UserBadgeResponse::from)
                .toList();
        
        return UserBadgeSummaryResponse.of(
                userId,
                (long) badgeResponses.size(),
                badgeResponses
        );
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
     * 주의: 호출 전에 이미 보유 여부를 확인하는 것이 좋습니다.
     */
    @Transactional
    public void awardBadge(User user, BadgeName badgeName) {
        Badge badge = badgeRepository.findByBadgeName(badgeName)
                .orElseGet(() -> {
                    Badge newBadge = Badge.builder()
                            .badgeName(badgeName)
                            .iconUrl(null) // 아이콘 URL은 별도 설정
                            .build();
                    return badgeRepository.save(newBadge);
                });
        
        if (userBadgeRepository.existsByUserIdAndBadgeName(user.getId(), badgeName)) {
            log.debug("User {} already has badge {}", user.getId(), badgeName);
            return;
        }
        
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


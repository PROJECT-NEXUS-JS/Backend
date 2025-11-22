package com.example.nexus.app.badge.repository;

import com.example.nexus.app.badge.domain.BadgeName;
import com.example.nexus.app.badge.domain.UserBadge;
import com.example.nexus.app.badge.dto.BadgeStatisticsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 뱃지 레포지토리
 */
@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    // ==================== 조회 쿼리 ====================
    
    /**
     * 사용자의 모든 뱃지 조회
     */
    @Query("SELECT ub FROM UserBadge ub " +
            "JOIN FETCH ub.badge " +
            "WHERE ub.user.id = :userId " +
            "ORDER BY ub.acquiredAt DESC")
    List<UserBadge> findAllByUserIdWithBadge(@Param("userId") Long userId);

    /**
     * 사용자의 뱃지 목록 조회
     */
    @Query(value = "SELECT ub FROM UserBadge ub " +
            "JOIN FETCH ub.badge " +
            "WHERE ub.user.id = :userId " +
            "ORDER BY ub.acquiredAt DESC",
            countQuery = "SELECT COUNT(ub) FROM UserBadge ub WHERE ub.user.id = :userId")
    Page<UserBadge> findPageByUserIdWithBadge(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 뱃지를 획득한 사용자 목록 조회
     */
    @Query("SELECT ub FROM UserBadge ub " +
            "JOIN FETCH ub.user " +
            "WHERE ub.badge.id = :badgeId " +
            "ORDER BY ub.acquiredAt DESC")
    List<UserBadge> findByBadgeIdWithUser(@Param("badgeId") Long badgeId);

    /**
     * 사용자의 특정 뱃지 조회 (BadgeName 기준)
     */
    @Query("SELECT ub FROM UserBadge ub " +
            "JOIN FETCH ub.badge " +
            "WHERE ub.user.id = :userId AND ub.badge.badgeName = :badgeName")
    Optional<UserBadge> findByUserIdAndBadgeName(@Param("userId") Long userId,
                                                 @Param("badgeName") BadgeName badgeName);

    // 존재 여부 확인
    
    /**
     * 사용자가 특정 뱃지를 보유하고 있는지 확인 (ID 기준)
     */
    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);

    @Query("SELECT COUNT(ub) > 0 FROM UserBadge ub " +
            "WHERE ub.user.id = :userId AND ub.badge.badgeName = :badgeName")
    boolean existsByUserIdAndBadgeName(@Param("userId") Long userId,
                                       @Param("badgeName") BadgeName badgeName);

    // 카운팅 쿼리
    
    /**
     * 사용자가 보유한 총 뱃지 수
     */
    long countByUserId(Long userId);

    /**
     * 특정 뱃지를 획득한 총 사용자 수
     */
    long countByBadgeId(Long badgeId);

    /**
     * 사용자가 특정 뱃지 목록 중 보유한 개수
     */
    @Query("SELECT COUNT(ub) FROM UserBadge ub " +
            "WHERE ub.user.id = :userId " +
            "AND ub.badge.badgeName IN :badgeNames")
    long countByUserIdAndBadgeNames(@Param("userId") Long userId,
                                    @Param("badgeNames") List<BadgeName> badgeNames);

    // 통계 및 분석 쿼리
    
    /**
     * 뱃지 통계 Top N
     * (※ enum 내부 필드를 JPQL로 접근하지 않도록 수정)
     */
    @Query("""
    SELECT new com.example.nexus.app.badge.dto.BadgeStatisticsDto(
        ub.badge.id,
        ub.badge.badgeName,
        COUNT(ub)
    )
    FROM UserBadge ub
    GROUP BY ub.badge.id, ub.badge.badgeName
    ORDER BY COUNT(ub) DESC
    """)
    List<BadgeStatisticsDto> findMostAcquiredBadges(Pageable pageable);

    @Query("SELECT ub FROM UserBadge ub " +
            "JOIN FETCH ub.user " +
            "WHERE ub.badge.badgeName = :badgeName " +
            "ORDER BY ub.acquiredAt DESC")
    List<UserBadge> findRecentUsersByBadgeName(@Param("badgeName") BadgeName badgeName,
                                               Pageable pageable);
}

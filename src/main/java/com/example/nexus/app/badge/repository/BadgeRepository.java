package com.example.nexus.app.badge.repository;

import com.example.nexus.app.badge.domain.Badge;
import com.example.nexus.app.badge.domain.BadgeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 뱃지 레포지토리
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    /**
     * 뱃지명으로 뱃지 조회
     */
    Optional<Badge> findByBadgeName(BadgeName badgeName);

    /**
     * 뱃지명 존재 여부 확인
     */
    boolean existsByBadgeName(BadgeName badgeName);

    /**
     * 여러 뱃지명으로 뱃지 목록 조회
     */
    List<Badge> findByBadgeNameIn(List<BadgeName> badgeNames);

    /**
     * 생성일 기준 오름차순 전체 조회
     */
    List<Badge> findAllByOrderByCreatedAtAsc();
}

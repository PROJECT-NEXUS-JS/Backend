package com.example.nexus.app.badge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뱃지 부여 조건 타입
 */
@Getter
@RequiredArgsConstructor
public enum BadgeConditionType {
    // 테스터 관련
    PARTICIPATION_APPROVED("테스트 참여 승인"),
    PARTICIPATION_COMPLETED("테스트 참여 완료"),
    
    // 리뷰어 관련
    REVIEW_CREATED("리뷰 작성"),
    FIRST_REVIEW_ON_POST("특정 테스트 첫 리뷰"),
    
    // 플래너 관련
    POST_PUBLISHED("테스트 모집 게시"),
    
    // 소통 관련 (기획자가 받은 리뷰 수)
    REVIEW_RECEIVED("리뷰 수신"),
    
    // 모집가 관련 (기획자의 누적 테스터 수)
    TESTER_COUNT_INCREASED("테스터 수 증가");
    
    private final String description;
}

package com.example.nexus.app.badge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뱃지 타입
 */
@Getter
@RequiredArgsConstructor
public enum BadgeType {
    TESTER("테스터", "테스트 참여 관련 뱃지"),
    REVIEWER("리뷰어", "리뷰 작성 관련 뱃지"),
    COMPLETER("완주자", "테스트 완료 관련 뱃지"),
    PIONEER("개척자", "특정 테스트 첫 리뷰 작성 관련 뱃지"),
    PLANNER("플래너", "테스트 모집 관련 뱃지"),
    COMMUNICATION("소통", "누적 리뷰 수신 관련 뱃지"),
    RECRUITER("모집가", "누적 테스터 수 관련 뱃지");

    private final String displayName;
    private final String description;
}

package com.example.nexus.app.badge.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 뱃지 목록 정의
 */
@Getter
@RequiredArgsConstructor
public enum BadgeName {
    
    // 테스터 뱃지 (테스트 참여 관련)
    NEWBIE_TESTER(
        "새내기 테스터",
        "첫 테스트에 참여한 경우",
        BadgeType.TESTER,
        BadgeConditionType.PARTICIPATION_APPROVED,
        1
    ),
    ROOKIE_TESTER(
        "루키 테스터",
        "테스트에 5회 이상 참여한 경우",
        BadgeType.TESTER,
        BadgeConditionType.PARTICIPATION_APPROVED,
        5
    ),
    BETA_TESTER(
        "베타 테스터",
        "테스트에 10회 이상 참여한 경우",
        BadgeType.TESTER,
        BadgeConditionType.PARTICIPATION_APPROVED,
        10
    ),
    
    // 리뷰어 뱃지 (개인 리뷰 작성 관련)
    FIRST_VOICE(
        "첫 목소리",
        "첫 리뷰를 작성한 경우",
        BadgeType.REVIEWER,
        BadgeConditionType.REVIEW_CREATED,
        1
    ),
    REVIEWER(
        "리뷰어",
        "리뷰를 5회 이상 작성한 경우",
        BadgeType.REVIEWER,
        BadgeConditionType.REVIEW_CREATED,
        5
    ),
    
    // 완주자 뱃지 (테스트 완료 관련)
    COMPLETER(
        "완주자",
        "첫 테스트를 완료한 경우",
        BadgeType.COMPLETER,
        BadgeConditionType.PARTICIPATION_COMPLETED,
        1
    ),
    STEADY_EXPERIMENTER(
        "꾸준한 실험가",
        "테스트를 5회 이상 완료한 경우",
        BadgeType.COMPLETER,
        BadgeConditionType.PARTICIPATION_COMPLETED,
        5
    ),
    ALPHA_MAIL(
        "알파메일",
        "테스트를 10회 이상 완료한 경우",
        BadgeType.COMPLETER,
        BadgeConditionType.PARTICIPATION_COMPLETED,
        10
    ),
    
    // 개척자 뱃지 (특정 테스트 첫 리뷰)
    PIONEER(
        "개척자",
        "특정 테스트에서 첫 리뷰를 작성한 경우",
        BadgeType.PIONEER,
        BadgeConditionType.FIRST_REVIEW_ON_POST,
        1
    ),
    
    // 플래너 뱃지 (테스트 모집 관련)
    TEST_PLANNER(
        "테스트 플래너",
        "첫 테스트를 모집한 경우",
        BadgeType.PLANNER,
        BadgeConditionType.POST_PUBLISHED,
        1
    ),
    TEST_LEADER(
        "테스트 리더",
        "테스트를 3회 이상 모집한 경우",
        BadgeType.PLANNER,
        BadgeConditionType.POST_PUBLISHED,
        3
    ),
    TEST_EXPERT(
        "테스트 전문가",
        "테스트를 10회 이상 모집한 경우",
        BadgeType.PLANNER,
        BadgeConditionType.POST_PUBLISHED,
        10
    ),
    
    // 소통 뱃지 (누적 리뷰 수신 관련 - 기획자 기준)
    COMMUNICATION_START(
        "소통의 시작",
        "누적 리뷰 10회 이상 작성된 경우",
        BadgeType.COMMUNICATION,
        BadgeConditionType.REVIEW_RECEIVED,
        10
    ),
    REVIEW_GARDEN(
        "리뷰의 정원",
        "누적 리뷰 30회 이상 작성된 경우",
        BadgeType.COMMUNICATION,
        BadgeConditionType.REVIEW_RECEIVED,
        30
    ),
    REVIEW_FOREST(
        "리뷰의 숲",
        "누적 리뷰 50회 이상 작성된 경우",
        BadgeType.COMMUNICATION,
        BadgeConditionType.REVIEW_RECEIVED,
        50
    ),
    
    // 모집가 뱃지 (누적 테스터 수 관련 - 기획자 기준)
    TESTER_RECRUITER(
        "테스터 모집가",
        "테스트에 참여한 테스터 수가 누적 10명 이상인 경우",
        BadgeType.RECRUITER,
        BadgeConditionType.TESTER_COUNT_INCREASED,
        10
    ),
    TESTER_SCOUTER(
        "테스터 스카우터",
        "테스트에 참여한 테스터 수가 누적 30명 이상인 경우",
        BadgeType.RECRUITER,
        BadgeConditionType.TESTER_COUNT_INCREASED,
        30
    ),
    TESTER_ALPHA_MAIL(
        "테스터의 알파메일",
        "테스트에 참여한 테스터 수가 누적 100명 이상인 경우",
        BadgeType.RECRUITER,
        BadgeConditionType.TESTER_COUNT_INCREASED,
        100
    );
    
    private final String displayName;
    private final String description;
    private final BadgeType badgeType;
    private final BadgeConditionType conditionType;
    private final Integer conditionValue;

    /**
     * 특정 조건 타입에 해당하는 모든 뱃지 조회
     */
    public static List<BadgeName> getByConditionType(BadgeConditionType conditionType) {
        return Arrays.stream(values())
            .filter(badge -> badge.conditionType == conditionType)
            .toList();
    }

    /**
     * 특정 타입의 모든 뱃지 조회
     */
    public static List<BadgeName> getByBadgeType(BadgeType badgeType) {
        return Arrays.stream(values())
            .filter(badge -> badge.badgeType == badgeType)
            .toList();
    }
}
